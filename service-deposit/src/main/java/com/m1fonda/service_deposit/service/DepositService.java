package com.m1fonda.service_deposit.service;

import java.util.UUID;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.m1fonda.commons_libs.config.*;
import com.m1fonda.service_deposit.dto.DepositRequest;
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
    public Deposit newDeposit(DepositRequest request){
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String transactionNum = uuid.substring(0, 10);
        Deposit deposit = Deposit.builder().transactionNum(transactionNum).accountNum(request.accountNum()).amount(request.amount()).build();
        depositRepository.save(deposit);
        rabbitTemplate.convertAndSend(RabbitMQConstants.ACCOUNT_EXCHANGE, RabbitMQConstants.ACCOUNT_DEPOSIT_KEY, request);
        return deposit;
    }

    public void depositFallback(){
        String message = "Deposit service is latent or down...";
        System.out.println(message);
        log.info(message);
    }
}
