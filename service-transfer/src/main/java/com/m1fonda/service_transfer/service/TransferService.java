package com.m1fonda.service_transfer.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.m1fonda.service_transfer.dto.TransferDTO;
import com.m1fonda.service_transfer.dto.TransferRequest;
import com.m1fonda.service_transfer.dto.TransferResponse;
import com.m1fonda.service_transfer.model.Transfer;
import com.m1fonda.service_transfer.repository.TransferRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.m1fonda.commons_libs.config.RabbitMQConstants;
import com.m1fonda.commons_libs.dto.AgencyDTO;


@Slf4j
@Service
@AllArgsConstructor
@CircuitBreaker(name="transferCircuitBreaker", fallbackMethod = "transferFallback")
public class TransferService {

    private final TransferRepository transferRepository;
    public final RabbitTemplate rabbitTemplate;

    @CircuitBreaker(name="transferCircuitBreaker", fallbackMethod = "transferFallback")
    public TransferResponse newTransfer(TransferRequest request){
        String transactionID = getId();
        Transfer transfer = Transfer.builder()
                                    .transactionNum(transactionID)
                                    .senderAccountNum(request.senderAccountNum())
                                    .receiverAccountNum(request.receiverAccountNum())
                                    .amount(request.amount())
                                    .fees(request.fees())
                                    .build();

        transferRepository.save(transfer);

        rabbitTemplate.convertAndSend(RabbitMQConstants.ACCOUNT_EXCHANGE, RabbitMQConstants.ACCOUNT_UPDATE_KEY, new  TransferDTO(request.receiverAccountNum(), request.amount()) );
        rabbitTemplate.convertAndSend(RabbitMQConstants.ACCOUNT_EXCHANGE, RabbitMQConstants.ACCOUNT_UPDATE_KEY, new  TransferDTO(request.senderAccountNum(), -1 *(request.amount()+request.fees())) );
        rabbitTemplate.convertAndSend(RabbitMQConstants.AGENCY_EXCHANGE, RabbitMQConstants.AGENCY_UPDATE_KEY, new  AgencyDTO(request.agencyNum(), null, request.fees(), 0, 0, null));

        return new TransferResponse(request.senderAccountNum(), request.receiverAccountNum(), request.agencyNum(), transactionID, request.amount(), request.fees(), new Date());
    }

    public List<TransferResponse> filterTransfers(String accountId, String agencyId) {
        if (accountId != null && agencyId != null) return TransferResponse.fromList(transferRepository.findBySenderAccountNumOrReceiverAccountNumAndAgencyNum(accountId, agencyId));
        if (agencyId != null) return TransferResponse.fromList(transferRepository.findByAgencyNum(agencyId));
        return TransferResponse.fromList(transferRepository.findBySenderAccountNumOrReceiverAccountNum(accountId));
    }

    public String getId(){
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid.substring(0, 10);
    }


    public void transferFallback(){
        String message = "Transfer service is latent or down...";
        System.out.println(message);
        log.info(message);
    }
}
