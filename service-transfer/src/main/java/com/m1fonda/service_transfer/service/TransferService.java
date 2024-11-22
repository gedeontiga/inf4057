package com.m1fonda.service_transfer.service;

import java.util.UUID;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.m1fonda.service_transfer.dto.TransferRequest;
import com.m1fonda.service_transfer.model.Transfer;
import com.m1fonda.service_transfer.repository.TransferRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.m1fonda.commons_libs.config.*;


@Slf4j
@Service
@AllArgsConstructor
@CircuitBreaker(name="transferCircuitBreaker", fallbackMethod = "transferFallback")
public class TransferService {

    private final TransferRepository transferRepository;
    public final RabbitTemplate rabbitTemplate;

    @CircuitBreaker(name="transferCircuitBreaker", fallbackMethod = "transferFallback")
    @RabbitListener(queues = RabbitMQConstants.TRANSFER_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public Transfer pendingTransfer(TransferRequest request){
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String transactionNum = uuid.substring(0, 10);
        Transfer transfer = Transfer.builder().transactionNum(transactionNum).senderAccountNum(request.senderAccountNum()).receiverAccountNum(request.receiverAccountNum()).amount(request.amount()).build();
        transferRepository.save(transfer);
        rabbitTemplate.convertAndSend(RabbitMQConstants.ACCOUNT_EXCHANGE, RabbitMQConstants.ACCOUNT_TRANSFER_KEY, request);
        return transfer;
    }

    public void transferFallback(){
        String message = "Transfer service is latent or down...";
        System.out.println(message);
        log.info(message);
    }
}
