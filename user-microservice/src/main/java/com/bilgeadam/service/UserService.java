package com.bilgeadam.service;

import com.bilgeadam.dto.request.EmployeeInfoUpdateDto;
import com.bilgeadam.dto.request.GuestInfoUpdateDto;
import com.bilgeadam.exception.ErrorType;
import com.bilgeadam.exception.UserManagerException;
import com.bilgeadam.mapper.IUserMapper;

import com.bilgeadam.rabbitmq.model.*;
import com.bilgeadam.rabbitmq.producer.AddEmloyeeProducer;
import com.bilgeadam.rabbitmq.producer.UserCompanyIdModelsProducer;
import com.bilgeadam.rabbitmq.model.UserCreateEmployeeModel;
import com.bilgeadam.rabbitmq.model.UserForgotPassModel;
import com.bilgeadam.rabbitmq.model.UserRegisterModel;
import com.bilgeadam.repository.IUserRepository;
import com.bilgeadam.repository.entity.User;
import com.bilgeadam.repository.enums.ERole;
import com.bilgeadam.repository.enums.EStatus;
import com.bilgeadam.utility.ServiceManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService extends ServiceManager<User, String> {
    private final IUserRepository userRepository;
    private final AddEmloyeeProducer addEmloyeeProducer;
    private final UserCompanyIdModelsProducer userCompanyIdModelsProducer;

    public UserService(IUserRepository userRepository, AddEmloyeeProducer addEmloyeeProducer, UserCompanyIdModelsProducer userCompanyIdModelsProducer) {

        super(userRepository);
        this.userRepository = userRepository;
        this.addEmloyeeProducer = addEmloyeeProducer;
        this.userCompanyIdModelsProducer = userCompanyIdModelsProducer;
    }

    public Boolean createUser(UserRegisterModel model) {
        Optional<User> optionalUser = userRepository.findOptionalByUsername(model.getUsername());
        if (optionalUser.isPresent()) {
            optionalUser.get().setStatus(EStatus.ACTIVE);
            update(optionalUser.get());
            return true;

        } else {
            User user = userRepository.save(IUserMapper.INSTANCE.fromRegisterModelToUserProfile(model));
            user.setRole(model.getRole());
            System.out.println(user);
            return true;
        }


    }

    public void forgotPassword(UserForgotPassModel userForgotPassModel) {

        Optional<User> optionalUser = userRepository.findOptionalByAuthid(userForgotPassModel.getAuthid());

        if (optionalUser.isEmpty()) {
            throw new UserManagerException(ErrorType.USER_NOT_FOUND);
        }
        optionalUser.get().setPassword(userForgotPassModel.getPassword());

    }

    public UserCreateEmployeeModel createEmployee(UserCreateEmployeeModel userCreateEmployeeModel) {
        if (userRepository.findOptionalByUsername(userCreateEmployeeModel.getUsername()).isPresent()) {
            throw new UserManagerException(ErrorType.USERNAME_DUPLICATE);
        }
        addEmloyeeProducer.sendAddEmployee(userCreateEmployeeModel);
        return userCreateEmployeeModel;
    }


    public void findByCompanyId(UserCompanyIdModel model) {
        List<UserCompanyListModel> companyIdModels = new ArrayList<>();
        List<User> userList = userRepository.findByCompanyId(model.getCompanyId());
        userList.forEach(x -> {
            UserCompanyListModel userCompanyListModel = IUserMapper.INSTANCE.userCompanyListModelFromUser(x);
            companyIdModels.add(userCompanyListModel);
        });
        userCompanyIdModelsProducer.sendUserList(companyIdModels);
    }


    // saving company manager as user (when registering new company)
    public Boolean createNewCompanyManager(CompanyManagerRegisterModel companyManagerRegisterModel) {
        User user = IUserMapper.INSTANCE.fromCompanyManagerRegisterModelToUser(companyManagerRegisterModel);
        user.setRole(ERole.COMPANY_MANAGER);
        save(user);
        return true;
    }

    public Optional<User> findEmployeeByAuthId(Long authId) {
        Optional<User> employee = userRepository.findOptionalByAuthid(authId);
        System.out.println("returning user is.... " +  employee);
        if (employee.isPresent()) {
            return employee;
        } else {
            throw new UserManagerException(ErrorType.USER_NOT_FOUND);
        }
    }

    // saving guest as user (when guest is registering)
    public Boolean createNewGuest(GuestRegisterModel guestRegisterModel) {
        User user = IUserMapper.INSTANCE.fromGuestRegisterModelToUser(guestRegisterModel);
        user.setRole(ERole.GUEST);
        save(user);
        return true;
    }

    // update employee profile information
    public Boolean updateEmployeeInfo(EmployeeInfoUpdateDto dto, Long authId) {
        Optional<User> employee = userRepository.findOptionalByAuthid(authId);
        if (employee.isPresent()) {
            userRepository.save(IUserMapper.INSTANCE.fromEmployeeInfoUpdateRequestDtoToUser(dto, employee.get()));
            return true;
        } else {
            throw new UserManagerException(ErrorType.USER_NOT_FOUND);
        }
    }

    // returns company id for getting company information of an employee (from user service)
    public String returnCompanyId(GetCompanyInformationModel getCompanyInformationModel) {
        Optional<User> optionalUser = userRepository.findOptionalByAuthid(getCompanyInformationModel.getAuthid());
        if (optionalUser.isPresent()) {
            return optionalUser.get().getCompanyId();
        }
        return null;
    }

    public String returnCompanyIdForComments(GetCompanyCommentsModel getCompanyCommentsModel) {
        Optional<User> optionalUser = userRepository.findOptionalByAuthid(getCompanyCommentsModel.getAuthid());
        if (optionalUser.isPresent()) {
            return optionalUser.get().getCompanyId();
        }
        return null;
    }

    public AddCommentUserAndCompanyResponseModel getUserIdAndCompanyId(AddCommentGetUserAndCompanyModel addCommentGetUserAndCompanyModel) {
        Optional<User> optionalUser = userRepository.findOptionalByAuthid(addCommentGetUserAndCompanyModel.getAuthid());
        if(optionalUser.isPresent()){
            AddCommentUserAndCompanyResponseModel addCommentUserAndCompanyResponseModel = new AddCommentUserAndCompanyResponseModel();
            addCommentUserAndCompanyResponseModel.setUserId(optionalUser.get().getId());
            addCommentUserAndCompanyResponseModel.setCompanyId(optionalUser.get().getCompanyId());
            return addCommentUserAndCompanyResponseModel;
        }
        return null;
    }

    // when adding new employee, returns company id based on company manager's auth id (from company service)
    public String returnCompanyIdForEmployee(AddEmployeeGetCompanyIdModel addEmployeeGetCompanyNameModel) {
        Optional<User> optionalUser = userRepository.findOptionalByAuthid(addEmployeeGetCompanyNameModel.getAuthid());
        if(optionalUser.isPresent()){
            return optionalUser.get().getCompanyId();
        }
        return null;
    }
    // saves new employee to user db
    public void addEmployeeSaveUser(AddEmployeeSaveUserModel addEmployeeSaveUserModel) {
        User user = IUserMapper.INSTANCE.fromAddEmployeeSaveUserModelToUser(addEmployeeSaveUserModel);
        save(user);
    }


    public Boolean updateGuestInfo(GuestInfoUpdateDto dto, Long authId) {
        Optional<User> employee = userRepository.findOptionalByAuthid(authId);
        if (employee.isPresent()) {
            userRepository.save(IUserMapper.INSTANCE.fromGuestInfoUpdateRequestDtoToUser(dto, employee.get()));
            return true;
        } else {
            throw new UserManagerException(ErrorType.USER_NOT_FOUND);
        }
    }
    // returns employee name and surname for getting pending comments (to comment service)
    public String returnEmployeeNameSurname(GetPendingCommentsEmployeeModel getPendingCommentsEmployeeModel) {
        Optional<User> optionalUser = userRepository.findById(getPendingCommentsEmployeeModel.getId());
        if(optionalUser.isPresent()){
            return optionalUser.get().getName() + " " + optionalUser.get().getSurname();
        }
        return null;
    }
    // returns company id to company manager for getting company info
    public String returnCompanyIdManager(GetCompanyInformationManagerModel getCompanyInformationManagerModel) {
        Optional<User> optionalUser = userRepository.findOptionalByAuthid(getCompanyInformationManagerModel.getAuthid());
        if(optionalUser.isPresent()){
            return optionalUser.get().getCompanyId();
        }
        return null;
    }

    public String returnCompanyIdManagerValuation(GetCompanyValuationManagerModel getCompanyValuationManagerModel) {
        Optional<User> optionalUser = userRepository.findOptionalByAuthid(getCompanyValuationManagerModel.getAuthid());
        if(optionalUser.isPresent()){
            return optionalUser.get().getCompanyId();
        }
        return null;
    }
}
