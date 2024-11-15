package com.m1fonda.service_bank.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.m1fonda.service_bank.model.BankRequest;
import com.m1fonda.service_bank.repository.BankRequestRepository;

import lombok.AllArgsConstructor;

@Component
@Service
@AllArgsConstructor
public class BankService {

    private final BankRequestRepository bankRequestRepository;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues =  "${rabbitmq.queue.name}", containerFactory = "rabbitListenerContainerFactory")
    public void agencyCreation(BankRequest bankRequest) {
        if (bankRequest.getAction() == "create-agency") {
            // if isValid(bankRequest){
            //     bankRequest.setStatus("APPROVED");
            //     rabbitTemplate.convertAndSend(null, null, bankRequest);
            // }
        }
    }

    public boolean isValid(BankRequest bankRequest) {
        return true;
    }


}
