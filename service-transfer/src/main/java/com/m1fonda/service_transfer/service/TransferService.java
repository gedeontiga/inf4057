package com.m1fonda.service_transfer.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
// import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.m1fonda.service_transfer.component.CustomRabbitTemplate;
import com.m1fonda.service_transfer.dto.TransferFilterDTO;
import com.m1fonda.service_transfer.model.Transfer;
import com.m1fonda.service_transfer.repository.TransferRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.m1fonda.commons_libs.config.*;
import com.m1fonda.commons_libs.dto.AccountDTO;
import com.m1fonda.commons_libs.dto.AccountTransferRequest;
import com.m1fonda.commons_libs.dto.AgencyTransactionDTO;
import com.m1fonda.commons_libs.dto.TransferRequest;
import com.m1fonda.commons_libs.dto.TransferRequestDTO;
import com.m1fonda.commons_libs.dto.TransferResponse;
import com.m1fonda.commons_libs.dto.UserResponse;


@Slf4j
@Service
@AllArgsConstructor
@CircuitBreaker(name="transferCircuitBreaker", fallbackMethod = "transferFallback")
public class TransferService {

    private final TransferRepository transferRepository;
    public final CustomRabbitTemplate rabbitTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    @CircuitBreaker(name="transferCircuitBreaker", fallbackMethod = "transferFallback")
    public TransferResponse newTransfer(TransferRequestDTO request){

        AccountDTO senderAccount = (AccountDTO) rabbitTemplate.convertSendAndReceive(RabbitMQConstants.ACCOUNT_EXCHANGE, RabbitMQConstants.ACCOUNT_READ_KEY, request.senderAccountNum());
        UserResponse sender = (UserResponse) rabbitTemplate.convertSendAndReceive(RabbitMQConstants.USER_EXCHANGE, RabbitMQConstants.USER_READ_KEY, senderAccount.userEmail());
        AgencyTransactionDTO senderAgency = (AgencyTransactionDTO) rabbitTemplate.convertSendAndReceive(RabbitMQConstants.AGENCY_EXCHANGE, RabbitMQConstants.AGENCY_READ_KEY, senderAccount.numAgency());

        AccountDTO receiverAccount = (AccountDTO) rabbitTemplate.convertSendAndReceive(RabbitMQConstants.ACCOUNT_EXCHANGE, RabbitMQConstants.ACCOUNT_READ_KEY, request.receiverAccountNum());
        UserResponse receiver = (UserResponse) rabbitTemplate.convertSendAndReceive(RabbitMQConstants.USER_EXCHANGE, RabbitMQConstants.USER_READ_KEY, receiverAccount.userEmail());
        AgencyTransactionDTO receiverAgency = (AgencyTransactionDTO) rabbitTemplate.convertSendAndReceive(RabbitMQConstants.AGENCY_EXCHANGE, RabbitMQConstants.AGENCY_READ_KEY, senderAccount.numAgency());

        double fees = getTransactionFees(request.amount(), senderAgency.bankNum() == receiverAgency.bankNum());

        AccountTransferRequest accountRequest = new AccountTransferRequest(request.senderAccountNum(), request.receiverAccountNum(), request.amount(), fees, 0, 0);
        
        AccountTransferRequest accountResponse = (AccountTransferRequest) rabbitTemplate.convertSendAndReceiveWithTimeout(RabbitMQConstants.ACCOUNT_EXCHANGE, RabbitMQConstants.ACCOUNT_TRANSFER_KEY, accountRequest, 30, TimeUnit.SECONDS);
        
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String transactionNum = uuid.substring(0, 10);
        
        TransferRequest response = new TransferRequest(
                                        request.senderAccountNum(), 
                                        senderAccount.numAgency(), 
                                        request.receiverAccountNum(), 
                                        receiverAccount.numAgency(), 
                                        sender.email(), 
                                        receiver.email(), 
                                        sender.firstName()+" "+sender.lastName(), 
                                        receiver.firstName()+" "+receiver.lastName(), 
                                        transactionNum, 
                                        transactionNum, request.amount(), 
                                        fees, 
                                        accountResponse.senderNewBalance(), 
                                        accountResponse.receiverNewBalance(), 
                                        new Date()
                                        );
                                        
        Transfer transfer = Transfer.builder().transactionNum(transactionNum).senderAccountNum(request.senderAccountNum()).receiverAccountNum(request.receiverAccountNum()).amount(request.amount()).build();
        transferRepository.save(transfer);

        rabbitTemplate.convertAndSend(RabbitMQConstants.AGENCY_EXCHANGE, RabbitMQConstants.AGENCY_TRANSFER_KEY, response);

        rabbitTemplate.convertAndSend(RabbitMQConstants.NOTIFICATION_EXCHANGE, RabbitMQConstants.EMAIL_TRANSFER_NOTIFICATION_KEY, response);
        rabbitTemplate.convertAndSend(RabbitMQConstants.NOTIFICATION_EXCHANGE, RabbitMQConstants.MESSAGE_DEPOSIT_NOTIFICATION_KEY, response);

        return new TransferResponse(request.senderAccountNum(), request.receiverAccountNum(), transactionNum, "initiated", request.amount(), new Date());
    }

    // @CircuitBreaker(name="transferApprovedCircuitBreaker", fallbackMethod = "transferFallback")
    // @RabbitListener(queues = RabbitMQConstants.TRANSFER_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    // public void approvedTransfer(TransferRequest request){
    //     TransferRequest accountResponse = (TransferRequest) rabbitTemplate.convertSendAndReceiveWithTimeout(RabbitMQConstants.ACCOUNT_EXCHANGE, RabbitMQConstants.ACCOUNT_TRANSFER_KEY, request, 30, TimeUnit.SECONDS);
    //     String uuid = UUID.randomUUID().toString().replace("-", "");
    //     String transactionNum = uuid.substring(0, 10);
    //     TransferRequest response = new TransferRequest(
    //                                     request.senderAccountNum(), 
    //                                     request.senderAgency(), 
    //                                     request.receiverAccountNum(), 
    //                                     request.receiverAgency(), 
    //                                     request.senderEmail(), 
    //                                     request.receiverEmail(), 
    //                                     request.senderName(), 
    //                                     request.receiverName(), 
    //                                     request.transactionNumber(), 
    //                                     transactionNum, 
    //                                     request.amount(), 
    //                                     accountResponse.fees(), 
    //                                     accountResponse.senderNewBalance(), 
    //                                     accountResponse.receiverNewBalance(), 
    //                                     new Date()
    //                                 );

    //     Transfer transfer = Transfer.builder().transactionNum(transactionNum).senderAccountNum(request.senderAccountNum()).receiverAccountNum(request.receiverAccountNum()).amount(request.amount()).build();
    //     transferRepository.save(transfer);
        
    //     rabbitTemplate.convertAndSend(RabbitMQConstants.NOTIFICATION_EXCHANGE, RabbitMQConstants.EMAIL_TRANSFER_NOTIFICATION_KEY, response);
    //     rabbitTemplate.convertAndSend(RabbitMQConstants.NOTIFICATION_EXCHANGE, RabbitMQConstants.MESSAGE_DEPOSIT_NOTIFICATION_KEY, response);

    // }

    public List<Transfer> filterTransfers(TransferFilterDTO filterDTO) {
        Query query = new Query(filterDTO.buildCriteria());
        return mongoTemplate.find(query, Transfer.class);
    }

    public double getTransactionFees(double amount, boolean type){
        double tarif = type? 1: 2;
        if (amount < 1000) return amount * 0.05 * tarif;
        else if (amount < 10000) return amount * 0.025 * tarif;
        else if (amount < 50000) return amount * 0.00125 * tarif;
        else if (amount < 500000) return amount * 0.00625 * tarif;
        else return amount * 0.004 * tarif;
    }


    public void transferFallback(){
        String message = "Transfer service is latent or down...";
        System.out.println(message);
        log.info(message);
    }
}
