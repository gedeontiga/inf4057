package com.m1fonda.service_transfer.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.m1fonda.service_transfer.dto.TransferRequest;
import com.m1fonda.service_transfer.dto.TransferResponse;
import com.m1fonda.service_transfer.model.Compte;
import com.m1fonda.service_transfer.model.Transfer;
import com.m1fonda.service_transfer.repository.CompteRepository;
import com.m1fonda.service_transfer.repository.TransferRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.m1fonda.commons_libs.config.RabbitMQConstants;
import com.m1fonda.commons_libs.dto.AccountDTO;
import com.m1fonda.commons_libs.dto.AgencyUpdateTransaction;
import com.m1fonda.commons_libs.dto.NotificationRequest;

@Slf4j
@Service
@AllArgsConstructor
@CircuitBreaker(name = "transferCircuitBreaker", fallbackMethod = "transferFallback")
public class TransferService {

        private final TransferRepository transferRepository;
        private final RabbitTemplate rabbitTemplate;
        private final CompteRepository compteRepository;

        @RabbitListener(bindings = @QueueBinding(value = @Queue(RabbitMQConstants.TRANSACTION_ACCOUNT_CREATION_QUEUE), exchange = @Exchange(value = RabbitMQConstants.TRANSFER_EXCHANGE, type = ExchangeTypes.DIRECT), key = RabbitMQConstants.TRANSACTION_ACCOUNT_CREATION_KEY))
        public void createAccount(Compte account) {
                Compte compte = Compte.builder()
                                .userEmail(account.getUserEmail())
                                .numAccount(account.getNumAccount())
                                .balance(account.getBalance())
                                .numAgency(account.getNumAgency())
                                .build();
                compteRepository.save(compte);
        }

        @CircuitBreaker(name = "transferCircuitBreaker", fallbackMethod = "transferFallback")
        public TransferResponse newTransfer(TransferRequest request) {
                String transactionID = getId();
                Transfer transfer = Transfer.builder()
                                .transactionNum(transactionID)
                                .senderAccountNum(request.senderAccountNum())
                                .receiverAccountNum(request.receiverAccountNum())
                                .amount(request.amount())
                                .fees(request.fees())
                                .agencyNum(request.agencyNum())
                                .createdAt(new Date())
                                .build();
                Compte accountSender = compteRepository.findByNumAccount(request.senderAccountNum())
                                .orElseThrow(() -> new EntityNotFoundException("Sender account not found"));
                Compte accountReciever = compteRepository.findByNumAccount(request.receiverAccountNum())
                                .orElseThrow(() -> new EntityNotFoundException("Reciever account not found"));
                double newBalance = accountSender.getBalance() - (request.amount() + request.amount() * request.fees());

                if (newBalance >= 0) {
                        accountSender.setBalance(newBalance);
                        accountReciever.setBalance(accountReciever.getBalance() + request.amount());
                        rabbitTemplate.convertAndSend(RabbitMQConstants.NOTIFICATION_EXCHANGE,
                                        RabbitMQConstants.NOTIFICATION_TRANSACTION_KEY,
                                        new NotificationRequest(null, accountSender.getUserEmail(),
                                                        accountSender.getNumAgency(),
                                                        "Transaction Réussie",
                                                        "Transaction effectuée avec succès.", new Date()));
                        rabbitTemplate.convertAndSend(RabbitMQConstants.NOTIFICATION_EXCHANGE,
                                        RabbitMQConstants.NOTIFICATION_TRANSACTION_KEY,
                                        new NotificationRequest(null, accountReciever.getUserEmail(),
                                                        accountReciever.getNumAgency(),
                                                        "Tranfert Réussie",
                                                        "Vous avez reçu un dépot de : " + request.amount()
                                                                        + " nouveau solde : "
                                                                        + accountReciever.getBalance(),
                                                        new Date()));

                        rabbitTemplate.convertAndSend(RabbitMQConstants.ACCOUNT_EXCHANGE,
                                        RabbitMQConstants.ACCOUNT_UPDATE_KEY,
                                        AccountDTO.builder().numAccount(request.senderAccountNum())
                                                        .balance(accountSender.getBalance()).build());
                        rabbitTemplate.convertAndSend(RabbitMQConstants.ACCOUNT_EXCHANGE,
                                        RabbitMQConstants.ACCOUNT_UPDATE_KEY,
                                        AccountDTO.builder().numAccount(request.receiverAccountNum())
                                                        .balance(accountReciever.getBalance()).build());

                        rabbitTemplate.convertAndSend(RabbitMQConstants.AGENCY_EXCHANGE, RabbitMQConstants.AGENCY_KEY,
                                        new AgencyUpdateTransaction(accountSender.getNumAgency(),
                                                        -1 * request.amount()));
                        rabbitTemplate.convertAndSend(RabbitMQConstants.AGENCY_EXCHANGE, RabbitMQConstants.AGENCY_KEY,
                                        new AgencyUpdateTransaction(accountReciever.getNumAgency(), request.amount()));
                } else {
                        rabbitTemplate.convertAndSend(RabbitMQConstants.NOTIFICATION_EXCHANGE,
                                        RabbitMQConstants.NOTIFICATION_TRANSACTION_KEY,
                                        new NotificationRequest(null, accountSender.getUserEmail(),
                                                        accountSender.getNumAgency(),
                                                        "Transaction Échouée",
                                                        "Désolé, votre solde est insuffisant pour effectuer cette transaction.",
                                                        new Date()));
                        throw new IllegalArgumentException("Balance Insuficient");
                }

                compteRepository.save(accountSender);
                compteRepository.save(accountReciever);
                transferRepository.save(transfer);

                return new TransferResponse(request.senderAccountNum(), request.receiverAccountNum(),
                                request.agencyNum(),
                                transactionID, accountSender.getBalance(), request.fees(), new Date());
        }

        public List<TransferResponse> filterTransfers(String accountId, String agencyId) {
                if (accountId != null && agencyId != null)
                        return TransferResponse.fromList(transferRepository
                                        .findBySenderAccountNumOrReceiverAccountNumAndAgencyNum(accountId, accountId,
                                                        agencyId));
                if (agencyId != null)
                        return TransferResponse.fromList(transferRepository.findByAgencyNum(agencyId));
                return TransferResponse
                                .fromList(transferRepository.findBySenderAccountNumOrReceiverAccountNum(accountId,
                                                accountId));
        }

        public List<TransferResponse> getAll() {
                return TransferResponse.fromList(transferRepository.findAll());
        }

        public void deleteAll() {
                transferRepository.deleteAll();
        }

        public String getId() {
                String uuid = UUID.randomUUID().toString().replace("-", "");
                return uuid.substring(0, 10);
        }

        public void transferFallback() {
                String message = "Transfer service is latent or down...";
                System.out.println(message);
                log.info(message);
        }
}
