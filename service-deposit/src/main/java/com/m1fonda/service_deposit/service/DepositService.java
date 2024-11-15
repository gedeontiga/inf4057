package com.m1fonda.service_deposit.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.m1fonda.dto.DepositRequest;
import com.m1fonda.dto.DepositResponse;
import com.m1fonda.service_deposit.model.Deposit;
import com.m1fonda.service_deposit.repository.DepositRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
@CircuitBreaker(name="depositCircuitBreaker", fallbackMethod = "depositFallback")
public class DepositService {

    private final DepositRepository depositRepository;
    public final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "depositPendingQueue", containerFactory = "rabbitListenerContainerFactory")
    public void pendingDeposit(DepositRequest request){
        rabbitTemplate.convertAndSend("transactionExchange", "accountQueue.routing.key", request);
    }

    @RabbitListener(queues = "depositApprovedQueue", containerFactory = "rabbitListenerContainerFactory")
    public void approvedDeposit(DepositResponse response){
        Deposit deposit = Deposit.builder().account(response.account()).amount(response.amount()).build();
        depositRepository.save(deposit);
        String message = "Deposit of "+deposit.getAmount()+"FCFA done. Account :" + deposit.getAccount()+". Transaction ID :"+deposit.getId();
        rabbitTemplate.convertAndSend("notificationExchange", "agencyNotificationQueue.routing.key", message);
        rabbitTemplate.convertAndSend("notificationExchange", "userNotificationQueue.routing.key", message);
    }

    public void depositFallback(){
        String message = "Deposit service is latent or down...";
        rabbitTemplate.convertAndSend("notificationExchange", "agencyNotificationQueue.routing.key", message);
        rabbitTemplate.convertAndSend("notificationExchange", "userNotificationQueue.routing.key", message);
        log.info(message);
    }
}
