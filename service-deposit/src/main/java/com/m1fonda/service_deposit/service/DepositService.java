package com.m1fonda.service_deposit.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.m1fonda.config.*;
import com.m1fonda.dto.DepositRequest;
import com.m1fonda.service_deposit.model.Deposit;
import com.m1fonda.service_deposit.repository.DepositRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class DepositService {

    private final DepositRepository depositRepository;
    public final RabbitTemplate rabbitTemplate;

    @CircuitBreaker(name="depositCircuitBreaker", fallbackMethod = "depositFallback")
    @RabbitListener(queues = RabbitMQConstants.DEPOSIT_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void newDeposit(DepositRequest request){
        Deposit deposit = Deposit.builder().account(request.account()).amount(request.amount()).build();
        depositRepository.save(deposit);
        rabbitTemplate.convertAndSend(RabbitMQConstants.ACCOUNT_EXCHANGE, RabbitMQConstants.EMAIL_DEPOSIT_NOTIFICATION_KEY, request);
    }

    public void depositFallback(){
        String message = "Deposit service is latent or down...";
        System.out.println(message);
        log.info(message);
    }
}
