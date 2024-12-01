package com.m1fonda.service_deposit.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.m1fonda.commons_libs.config.*;
// import com.m1fonda.commons_libs.dto.DepositResponse;
// import com.m1fonda.commons_libs.dto.UserResponse;
import com.m1fonda.commons_libs.dto.AccountDepositWithdrawalResponse;
// import com.m1fonda.commons_libs.dto.DepositRequest;
import com.m1fonda.service_deposit.component.CustomRabbitTemplate;
import com.m1fonda.service_deposit.dto.DepositFilterDTO;
// import com.m1fonda.service_deposit.dto.AccountDTO;
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
    public final CustomRabbitTemplate rabbitTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    // @CircuitBreaker(name="depositCircuitBreaker", fallbackMethod = "depositFallback")
    // public DepositResponse newDeposit(DepositRequest request){

    //     AccountDTO account = (AccountDTO) rabbitTemplate.convertSendAndReceive(RabbitMQConstants.ACCOUNT_EXCHANGE, RabbitMQConstants.ACCOUNT_READ_KEY, request.accountNum());
    //     UserResponse user = (UserResponse) rabbitTemplate.convertSendAndReceive(RabbitMQConstants.USER_EXCHANGE, RabbitMQConstants.USER_READ_KEY, account.userEmail());

    //     String status = "initiated";
    //     String transaction = "Deposit Request";

    //     AccountDepositWithdrawalResponse newRequest = new AccountDepositWithdrawalResponse(null, status, request.accountNum(), user.firstName()+" "+user.lastName(), account.numAgency(), user.email(), request.amount(), 0, 0, new Date());

    //     rabbitTemplate.convertAndSend(RabbitMQConstants.AGENCY_EXCHANGE, RabbitMQConstants.AGENCY_DEPOSIT_KEY, newRequest);

    //     return  new DepositResponse(request.accountNum(), transaction, request.amount(), status, new Date());
    // }

    @CircuitBreaker(name="depositApprovedCircuitBreaker", fallbackMethod = "depositFallback")
    @RabbitListener(queues = RabbitMQConstants.DEPOSIT_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void approvedDeposits(AccountDepositWithdrawalResponse request) {

        String uuid = UUID.randomUUID().toString().replace("-", "");
        String transactionID = uuid.substring(0, 10);

        try {
            AccountDepositWithdrawalResponse accountResponse = (AccountDepositWithdrawalResponse) rabbitTemplate.convertSendAndReceiveWithTimeout(RabbitMQConstants.ACCOUNT_EXCHANGE, RabbitMQConstants.ACCOUNT_DEPOSIT_KEY, request, 60, TimeUnit.SECONDS);
            Deposit deposit = Deposit.builder().transactionNum(transactionID).accountNum(request.numeroCompte()).amount(request.transactionAmount()).build();
            depositRepository.save(deposit);
            AccountDepositWithdrawalResponse finalResponse = new AccountDepositWithdrawalResponse(transactionID, "success", accountResponse.numeroCompte(), request.userName(), request.agencyID(),request.email(), accountResponse.transactionAmount(), 0, accountResponse.newBalance(), new Date()) ;
            rabbitTemplate.convertAndSend(RabbitMQConstants.NOTIFICATION_EXCHANGE, RabbitMQConstants.MESSAGE_DEPOSIT_NOTIFICATION_KEY, finalResponse);
            rabbitTemplate.convertAndSend(RabbitMQConstants.NOTIFICATION_EXCHANGE, RabbitMQConstants.EMAIL_DEPOSIT_NOTIFICATION_KEY, finalResponse);
        }
        catch (Exception e){
            AccountDepositWithdrawalResponse finalResponse = new AccountDepositWithdrawalResponse(transactionID, e.toString(), request.numeroCompte(), request.userName(), request.agencyID(), request.email(), request.transactionAmount(), 0, 0, new Date());
            rabbitTemplate.convertAndSend(RabbitMQConstants.NOTIFICATION_EXCHANGE, RabbitMQConstants.MESSAGE_DEPOSIT_NOTIFICATION_KEY, finalResponse);
            rabbitTemplate.convertAndSend(RabbitMQConstants.NOTIFICATION_EXCHANGE, RabbitMQConstants.EMAIL_DEPOSIT_NOTIFICATION_KEY, finalResponse);
        }
    }

    public List<Deposit> filterDeposits(DepositFilterDTO filterDTO) {
        Query query = new Query(filterDTO.buildCriteria());
        return mongoTemplate.find(query, Deposit.class);
    }

    public void depositFallback(){
        String message = "Deposit service is latent or down...";
        System.out.println(message);
        log.info(message);
    }
}
