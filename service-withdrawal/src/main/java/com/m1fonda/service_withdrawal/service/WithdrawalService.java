package com.m1fonda.service_withdrawal.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
// import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.m1fonda.service_withdrawal.component.CustomRabbitTemplate;
import com.m1fonda.service_withdrawal.dto.WithdrawalFilterDTO;
import com.m1fonda.service_withdrawal.model.Withdrawal;
import com.m1fonda.service_withdrawal.repository.WithdrawalRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.m1fonda.commons_libs.config.*;
import com.m1fonda.commons_libs.dto.AccountDTO;
import com.m1fonda.commons_libs.dto.AccountDepositWithdrawalResponse;
import com.m1fonda.commons_libs.dto.UserResponse;
import com.m1fonda.commons_libs.dto.WithdrawalRequest;
import com.m1fonda.commons_libs.dto.WithdrawalResponse;


@Slf4j
@Service
@AllArgsConstructor
@CircuitBreaker(name="withdrawalCircuitBreaker", fallbackMethod = "withdrawalFallback")
public class WithdrawalService {

    private final WithdrawalRepository withdrawalRepository;
    public final CustomRabbitTemplate rabbitTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    @CircuitBreaker(name="withdrawalCircuitBreaker", fallbackMethod = "withdrawalFallback")
    // @RabbitListener(queues = RabbitMQConstants.WITHDRAW_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public WithdrawalResponse newWithdrawal(WithdrawalRequest request){
        
        String status = "";
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String transactionNum = uuid.substring(0, 10);

        AccountDTO account = (AccountDTO) rabbitTemplate.convertSendAndReceive(RabbitMQConstants.ACCOUNT_EXCHANGE, RabbitMQConstants.ACCOUNT_READ_KEY, request.accountNum());
        UserResponse user = (UserResponse) rabbitTemplate.convertSendAndReceive(RabbitMQConstants.USER_EXCHANGE, RabbitMQConstants.USER_READ_KEY, account.userEmail());

        try{
            status = "initiated";
            double fees = getTransactionFees(request.amount());

            AccountDepositWithdrawalResponse newRequest = new AccountDepositWithdrawalResponse(transactionNum, status, request.accountNum(), user.firstName()+" "+user.lastName(), request.accountNum(), user.email(), request.amount(), fees, 0, new Date());

            AccountDepositWithdrawalResponse confirmedWithdraw = (AccountDepositWithdrawalResponse) rabbitTemplate.convertSendAndReceiveWithTimeout(RabbitMQConstants.ACCOUNT_EXCHANGE, RabbitMQConstants.ACCOUNT_WITHDRAW_KEY, newRequest, 30, TimeUnit.SECONDS);
            
            rabbitTemplate.convertAndSend(RabbitMQConstants.AGENCY_EXCHANGE, RabbitMQConstants.AGENCY_WITHDRAW_KEY, confirmedWithdraw);
            
            Withdrawal withdrawal = Withdrawal.builder().transactionNum(transactionNum).accountNum(request.accountNum()).amount(request.amount()).build();
            withdrawalRepository.save(withdrawal);

            rabbitTemplate.convertAndSend(RabbitMQConstants.NOTIFICATION_EXCHANGE, RabbitMQConstants.MESSAGE_WITHDRAWAL_NOTIFICATION_KEY, confirmedWithdraw);
            rabbitTemplate.convertAndSend(RabbitMQConstants.NOTIFICATION_EXCHANGE, RabbitMQConstants.EMAIL_WITHDRAWAL_NOTIFICATION_KEY, confirmedWithdraw);
            
        } catch (Exception e) {
            status = "failed";
        }

        return new WithdrawalResponse(request.accountNum(), transactionNum, request.amount(), status, new Date());
    }

    public List<Withdrawal> filterWithdrawals(WithdrawalFilterDTO filterDTO) {
        Query query = new Query(filterDTO.buildCriteria());
        return mongoTemplate.find(query, Withdrawal.class);
    }

    public double getTransactionFees(double amount){
        if (amount < 1000) return amount * 0.075;
        else if (amount < 10000) return amount * 0.05;
        else if (amount < 50000) return amount * 0.025;
        else if (amount < 500000) return amount * 0.0125;
        else return amount * 0.00625;
    }

    public void withdrawalFallback(){
        String message = "Withdrawal service is latent or down...";
        System.out.println(message);
        log.info(message);
    }
}
