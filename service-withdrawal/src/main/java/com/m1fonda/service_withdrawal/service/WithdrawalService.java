package com.m1fonda.service_withdrawal.service;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.m1fonda.service_withdrawal.component.CustomRabbitTemplate;
import com.m1fonda.service_withdrawal.model.Withdrawal;
import com.m1fonda.service_withdrawal.repository.WithdrawalRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.m1fonda.commons_libs.config.*;
import com.m1fonda.commons_libs.dto.AccountDepositWithdrawalResponse;
import com.m1fonda.commons_libs.dto.WithdrawalResponse;


@Slf4j
@Service
@AllArgsConstructor
@CircuitBreaker(name="withdrawalCircuitBreaker", fallbackMethod = "withdrawalFallback")
public class WithdrawalService {

    private final WithdrawalRepository withdrawalRepository;
    public final CustomRabbitTemplate rabbitTemplate;

    @CircuitBreaker(name="withdrawalCircuitBreaker", fallbackMethod = "withdrawalFallback")
    @RabbitListener(queues = RabbitMQConstants.WITHDRAW_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public WithdrawalResponse newWithdrawal(AccountDepositWithdrawalResponse request){
        
        String status = "";
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String transactionNum = uuid.substring(0, 10);

        try{
            double fees = request.transactionAmount() * 0.1;

            AccountDepositWithdrawalResponse newRequest = new AccountDepositWithdrawalResponse(transactionNum, status, request.numeroCompte(), request.userName(), request.agencyID(), request.email(), request.transactionAmount(), fees, 0, new Date());

            AccountDepositWithdrawalResponse confirmedWithdraw = (AccountDepositWithdrawalResponse) rabbitTemplate.convertSendAndReceiveWithTimeout(RabbitMQConstants.ACCOUNT_EXCHANGE, RabbitMQConstants.ACCOUNT_WITHDRAW_KEY, newRequest, 30, TimeUnit.SECONDS);
            
            rabbitTemplate.convertAndSend(RabbitMQConstants.AGENCY_EXCHANGE, RabbitMQConstants.AGENCY_WITHDRAW_KEY, newRequest);
            
            Withdrawal withdrawal = Withdrawal.builder().transactionNum(transactionNum).accountNum(request.numeroCompte()).amount(request.transactionAmount()).build();
            withdrawalRepository.save(withdrawal);
            
            status = "success";

            AccountDepositWithdrawalResponse response = new AccountDepositWithdrawalResponse(transactionNum, status, request.numeroCompte(), request.userName(), request.agencyID(), request.email(), request.transactionAmount(), fees, confirmedWithdraw.newBalance(), new Date());
            

            rabbitTemplate.convertAndSend(RabbitMQConstants.NOTIFICATION_EXCHANGE, RabbitMQConstants.MESSAGE_WITHDRAWAL_NOTIFICATION_KEY, response);
            rabbitTemplate.convertAndSend(RabbitMQConstants.NOTIFICATION_EXCHANGE, RabbitMQConstants.EMAIL_WITHDRAWAL_NOTIFICATION_KEY, response);
            
        } catch (Exception e) {
            status = "failed";
        }

        return new WithdrawalResponse(request.numeroCompte(), transactionNum, request.transactionAmount(), status, new Date());
    }

    public void withdrawalFallback(){
        String message = "Withdrawal service is latent or down...";
        System.out.println(message);
        log.info(message);
    }
}
