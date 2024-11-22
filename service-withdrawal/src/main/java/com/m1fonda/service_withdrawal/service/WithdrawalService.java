package com.m1fonda.service_withdrawal.service;

import java.util.UUID;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.m1fonda.service_withdrawal.dto.WithdrawRequest;
import com.m1fonda.service_withdrawal.model.Withdrawal;
import com.m1fonda.service_withdrawal.repository.WithdrawalRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.m1fonda.commons_libs.config.*;


@Slf4j
@Service
@AllArgsConstructor
@CircuitBreaker(name="withdrawalCircuitBreaker", fallbackMethod = "withdrawalFallback")
public class WithdrawalService {

    private final WithdrawalRepository withdrawalRepository;
    public final RabbitTemplate rabbitTemplate;

    @CircuitBreaker(name="withdrawalCircuitBreaker", fallbackMethod = "withdrawalFallback")
    @RabbitListener(queues = RabbitMQConstants.WITHDRAW_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void pendingWithdrawal(WithdrawRequest request){
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String transactionNum = uuid.substring(0, 10);
        Withdrawal withdrawal = Withdrawal.builder().transactionNum(transactionNum).accountNum(request.accountNum()).amount(request.amount()).build();
        withdrawalRepository.save(withdrawal);
        rabbitTemplate.convertAndSend(RabbitMQConstants.ACCOUNT_EXCHANGE, RabbitMQConstants.ACCOUNT_WITHDRAW_KEY, request);
    }

    public void withdrawalFallback(){
        String message = "Withdrawal service is latent or down...";
        System.out.println(message);
        log.info(message);
    }
}
