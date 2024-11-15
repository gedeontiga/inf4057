package com.m1fonda.service_withdrawal.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.m1fonda.dto.WithdrawalRequest;
import com.m1fonda.dto.WithdrawalResponse;
import com.m1fonda.service_withdrawal.model.Withdrawal;
import com.m1fonda.service_withdrawal.repository.WithdrawalRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
@CircuitBreaker(name="withdrawalCircuitBreaker", fallbackMethod = "withdrawalFallback")
public class WithdrawalService {

    private final WithdrawalRepository withdrawalRepository;
    public final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "withdrawalPendingQueue", containerFactory = "rabbitListenerContainerFactory")
    public void pendingWithdrawal(WithdrawalRequest request){
        rabbitTemplate.convertAndSend("transactionExchange", "accountQueue.routing.key", request);
    }

    @RabbitListener(queues = "withdrawalApprovedQueue", containerFactory = "rabbitListenerContainerFactory")
    public void approvedWithdrawal(WithdrawalResponse response){
        Withdrawal withdrawal = Withdrawal.builder().account(response.account()).amount(response.amount()).build();
        withdrawalRepository.save(withdrawal);
        String message = "Withdrawal of "+withdrawal.getAmount()+"FCFA done. Account :" + withdrawal.getAccount()+". Transaction ID :"+withdrawal.getId();
        rabbitTemplate.convertAndSend("notificationExchange", "agencyNotificationQueue.routing.key", message);
        rabbitTemplate.convertAndSend("notificationExchange", "userNotificationQueue.routing.key", message);
    }

    public void withdrawalFallback(){
        String message = "Withdrawal service is latent or down...";
        rabbitTemplate.convertAndSend("notificationExchange", "agencyNotificationQueue.routing.key", message);
        rabbitTemplate.convertAndSend("notificationExchange", "userNotificationQueue.routing.key", message);
        log.info(message);
    }
}
