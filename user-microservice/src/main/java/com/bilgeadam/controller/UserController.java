package com.bilgeadam.controller;

import com.bilgeadam.rabbitmq.model.UserCreateEmployeeModel;
import com.bilgeadam.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static com.bilgeadam.constant.ApiUrls.*;


@RestController
@RequiredArgsConstructor
@RequestMapping(USER)
public class UserController {

    private final UserService userService;

    //Geçici Süreliğine Devre DISIYIZZZ!!(K:T)
//    @PutMapping(FORGOT_PASSWORD)
//    public ResponseEntity<Boolean> forgotPassword(@RequestBody UserForgotPassModel userForgotPassModel){
//        return ResponseEntity.ok(userService.forgotPassword(userForgotPassModel));
//    }
    @PostMapping(CREATEEMPLOYEE)
    public ResponseEntity<UserCreateEmployeeModel> addEmployee(@RequestBody UserCreateEmployeeModel userAddEmployeeModel){
        return ResponseEntity.ok(userService.createEmployee(userAddEmployeeModel));
    }

}
