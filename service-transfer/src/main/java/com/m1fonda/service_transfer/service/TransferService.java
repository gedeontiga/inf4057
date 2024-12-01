package com.m1fonda.service_transfer.service;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.m1fonda.service_transfer.component.CustomRabbitTemplate;
import com.m1fonda.service_transfer.model.Transfer;
import com.m1fonda.service_transfer.repository.TransferRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.m1fonda.commons_libs.config.*;
import com.m1fonda.commons_libs.dto.TransferRequest;
import com.m1fonda.commons_libs.dto.TransferResponse;


@Slf4j
@Service
@AllArgsConstructor
@CircuitBreaker(name="transferCircuitBreaker", fallbackMethod = "transferFallback")
public class TransferService {

    private final TransferRepository transferRepository;
    public final CustomRabbitTemplate rabbitTemplate;

    @CircuitBreaker(name="transferCircuitBreaker", fallbackMethod = "transferFallback")
    public TransferResponse newTransfer(TransferRequest request){
        rabbitTemplate.convertAndSend(RabbitMQConstants.AGENCY_EXCHANGE, RabbitMQConstants.AGENCY_TRANSFER_KEY, request);
        return new TransferResponse(request.senderAccountNum(), request.receiverAccountNum(), null, "initiated", request.amount(), new Date());
    }

    @CircuitBreaker(name="transferApprovedCircuitBreaker", fallbackMethod = "transferFallback")
    @RabbitListener(queues = RabbitMQConstants.TRANSFER_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void approvedTransfer(TransferRequest request){
        TransferRequest accountResponse = (TransferRequest) rabbitTemplate.convertSendAndReceiveWithTimeout(RabbitMQConstants.ACCOUNT_EXCHANGE, RabbitMQConstants.ACCOUNT_TRANSFER_KEY, request, 30, TimeUnit.SECONDS);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String transactionNum = uuid.substring(0, 10);
        TransferRequest response = new TransferRequest(
                                        request.senderAccountNum(), 
                                        request.senderAgency(), 
                                        request.receiverAccountNum(), 
                                        request.receiverAgency(), 
                                        request.senderEmail(), 
                                        request.receiverEmail(), 
                                        request.senderName(), 
                                        request.receiverName(), 
                                        request.transactionNumber(), 
                                        transactionNum, 
                                        request.amount(), 
                                        accountResponse.fees(), 
                                        accountResponse.senderNewBalance(), 
                                        accountResponse.receiverNewBalance(), 
                                        new Date());

        Transfer transfer = Transfer.builder().transactionNum(transactionNum).senderAccountNum(request.senderAccountNum()).receiverAccountNum(request.receiverAccountNum()).amount(request.amount()).build();
        transferRepository.save(transfer);
        
        rabbitTemplate.convertAndSend(RabbitMQConstants.NOTIFICATION_EXCHANGE, RabbitMQConstants.EMAIL_TRANSFER_NOTIFICATION_KEY, response);
        rabbitTemplate.convertAndSend(RabbitMQConstants.NOTIFICATION_EXCHANGE, RabbitMQConstants.MESSAGE_DEPOSIT_NOTIFICATION_KEY, response);

    }


    public void transferFallback(){
        String message = "Transfer service is latent or down...";
        System.out.println(message);
        log.info(message);
    }
}
