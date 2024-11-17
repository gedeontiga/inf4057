package com.m1fonda.service_withdrawal.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.m1fonda.dto.WithdrawalRequest;
import com.m1fonda.service_withdrawal.model.Withdrawal;
import com.m1fonda.service_withdrawal.repository.WithdrawalRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.m1fonda.config.*;


@Slf4j
@Service
@AllArgsConstructor
@CircuitBreaker(name="withdrawalCircuitBreaker", fallbackMethod = "withdrawalFallback")
public class WithdrawalService {

    private final WithdrawalRepository withdrawalRepository;
    public final RabbitTemplate rabbitTemplate;

    @CircuitBreaker(name="withdrawalCircuitBreaker", fallbackMethod = "withdrawalFallback")
    @RabbitListener(queues = RabbitMQConstants.WITHDRAW_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void pendingWithdrawal(WithdrawalRequest request){
        Withdrawal withdrawal = Withdrawal.builder().account(request.account()).amount(request.amount()).build();
        withdrawalRepository.save(withdrawal);
        rabbitTemplate.convertAndSend(RabbitMQConstants.NOTIFICATION_EXCHANGE, RabbitMQConstants.EMAIL_WITHDRAWAL_NOTIFICATION_KEY, request);
    }

    public void withdrawalFallback(){
        String message = "Withdrawal service is latent or down...";
        System.out.println(message);
        log.info(message);
    }
}
