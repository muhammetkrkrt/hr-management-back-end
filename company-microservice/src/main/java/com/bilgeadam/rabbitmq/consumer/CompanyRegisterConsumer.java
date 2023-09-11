package com.bilgeadam.rabbitmq.consumer;

import com.bilgeadam.rabbitmq.model.CompanyRegisterModel;
import com.bilgeadam.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyRegisterConsumer {

    private final CompanyService companyService;
    @RabbitListener(queues = "company-register-queue")
    public void createNewCompany(CompanyRegisterModel companyRegisterModel){
        System.out.println("company register model consumer: " + companyRegisterModel);
        companyService.createNewCompany(companyRegisterModel);
    }
}
