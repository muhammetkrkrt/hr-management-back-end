package com.bilgeadam.mapper;
import com.bilgeadam.dto.request.AddCommentRequestDto;
import com.bilgeadam.dto.request.AddEmployeeCompanyDto;
import com.bilgeadam.dto.request.CompanyUpdateRequestDto;
import com.bilgeadam.rabbitmq.model.*;
import com.bilgeadam.repository.entity.Company;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ICompanyMapper {
    ICompanyMapper INSTANCE = Mappers.getMapper(ICompanyMapper.class);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Company fromCompanyUpdateDto(final CompanyUpdateRequestDto dto, @MappingTarget Company company);
    @Mapping(source = "id",target = "companyId")
    UserCompanyIdModel fromCompanyToUserCompanyIdModel(final Company company);

    AddEmployeeCompanyModel addEmployeeCompanyModelfromAddEmployeeCompanyDto(final AddEmployeeCompanyDto addEmployeeCompanyDto);


    Company fromCompanyRegisterModelToCompany(final CompanyRegisterModel companyRegisterModel);

    AddCommentSaveCommentModel fromAddCommentRequestDtoToAddCommentSaveCommentModel(final AddCommentRequestDto addCommentRequestDto);
}
