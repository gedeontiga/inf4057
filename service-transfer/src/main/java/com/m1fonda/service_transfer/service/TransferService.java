package com.m1fonda.service_transfer.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.m1fonda.service_transfer.dto.AccountNotFoundException;
import com.m1fonda.service_transfer.dto.InsufficientBalanceException;
import com.m1fonda.service_transfer.dto.TransactionValidationException;
import com.m1fonda.service_transfer.dto.TransferRequest;
import com.m1fonda.service_transfer.dto.TransferResponse;
import com.m1fonda.service_transfer.model.Compte;
import com.m1fonda.service_transfer.model.Transfer;
import com.m1fonda.service_transfer.repository.CompteRepository;
import com.m1fonda.service_transfer.repository.TransferRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.m1fonda.commons_libs.config.RabbitMQConstants;
import com.m1fonda.commons_libs.dto.AccountDTO;
import com.m1fonda.commons_libs.dto.AccountTransactionDTO;
import com.m1fonda.commons_libs.dto.NotificationRequest;
import com.m1fonda.commons_libs.dto.TransactionUpdateAccount;

@Slf4j
@Service
@AllArgsConstructor
@CircuitBreaker(name = "transferCircuitBreaker", fallbackMethod = "transferFallback")
public class TransferService {

        private final TransferRepository transferRepository;
        private final RabbitTemplate rabbitTemplate;
        private final CompteRepository compteRepository;

        @RabbitListener(queues = RabbitMQConstants.TRANSFER_ACCOUNT_CREATION_QUEUE)
        public void createAccount(AccountTransactionDTO account) {
                try {
                        Compte compte = Compte.builder()
                                        .userEmail(account.userEmail())
                                        .numAccount(account.numAccount())
                                        .balance(account.balance())
                                        .numAgency(account.numAgency())
                                        .build();
                        compteRepository.save(compte);
                } catch (Exception e) {
                        log.error("Error creating account in deposit service: ", e);
                        throw new TransactionValidationException("Failed to create account: " + e.getMessage());
                }
        }

        @RabbitListener(queues = RabbitMQConstants.TRANSFER_ACCOUNT_UPDATE_QUEUE)
        public void updateAccount(TransactionUpdateAccount account) {
                Compte compte = compteRepository.findByNumAccount(account.numAccount())
                                .orElseThrow(() -> new RuntimeException("Account not found"));
                compte.setBalance(account.balance());
                compteRepository.save(compte);
        }

        public TransferResponse newTransfer(TransferRequest request) {
                validateTransferRequest(request);

                String transactionID = getId();
                Compte accountSender = compteRepository.findByNumAccount(request.senderAccountNum())
                                .orElseThrow(() -> new AccountNotFoundException(
                                                "Sender account not found: " + request.senderAccountNum()));

                Compte accountReceiver = compteRepository.findByNumAccount(request.receiverAccountNum())
                                .orElseThrow(() -> new AccountNotFoundException(
                                                "Receiver account not found: " + request.receiverAccountNum()));

                double totalAmount = request.amount() + (request.amount() * request.fees());
                double newBalance = accountSender.getBalance() - totalAmount;

                if (newBalance < 0) {
                        throw new InsufficientBalanceException("Insufficient balance for transfer. Required: " +
                                        totalAmount + ", Available: " + accountSender.getBalance());
                }

                try {
                        executeTransfer(accountSender, accountReceiver, request, newBalance, transactionID);
                        return createTransferResponse(request, accountSender, transactionID);
                } catch (Exception e) {
                        log.error("Error processing transfer: ", e);
                        throw new TransactionValidationException("Failed to process transfer: " + e.getMessage());
                }
        }

        private void validateTransferRequest(TransferRequest request) {
                if (request.amount() <= 0) {
                        throw new TransactionValidationException("Transfer amount must be greater than zero");
                }
                if (request.senderAccountNum().equals(request.receiverAccountNum())) {
                        throw new TransactionValidationException("Sender and receiver accounts cannot be the same");
                }
                if (request.fees() < 0) {
                        throw new TransactionValidationException("Fees cannot be negative");
                }
        }

        private void executeTransfer(Compte sender, Compte receiver, TransferRequest request,
                        double newSenderBalance, String transactionID) {
                // Update balances
                sender.setBalance(newSenderBalance);
                receiver.setBalance(receiver.getBalance() + request.amount());

                // Save transfer record
                Transfer transfer = Transfer.builder()
                                .transactionNum(transactionID)
                                .senderAccountNum(request.senderAccountNum())
                                .receiverAccountNum(request.receiverAccountNum())
                                .amount(request.amount())
                                .fees(request.fees())
                                .agencyNum(request.agencyNum())
                                .createdAt(new Date())
                                .build();

                // Send notifications and updates
                sendNotifications(sender, receiver, request);
                updateAccounts(sender, receiver);

                // Save all changes
                compteRepository.save(sender);
                compteRepository.save(receiver);
                transferRepository.save(transfer);
        }

        private TransferResponse createTransferResponse(TransferRequest request, Compte sender,
                        String transactionID) {
                return new TransferResponse(
                                request.senderAccountNum(),
                                request.receiverAccountNum(),
                                request.agencyNum(),
                                transactionID,
                                sender.getBalance(),
                                request.fees(),
                                new Date());
        }

        private void updateAccounts(Compte sender, Compte receiver) {
                // Update sender's account in other services
                rabbitTemplate.convertAndSend(
                                RabbitMQConstants.ACCOUNT_EXCHANGE,
                                RabbitMQConstants.ACCOUNT_UPDATE_KEY,
                                AccountDTO.builder()
                                                .numAccount(sender.getNumAccount())
                                                .balance(sender.getBalance())
                                                .build());

                // Update receiver's account in other services
                rabbitTemplate.convertAndSend(
                                RabbitMQConstants.ACCOUNT_EXCHANGE,
                                RabbitMQConstants.ACCOUNT_UPDATE_KEY,
                                AccountDTO.builder()
                                                .numAccount(receiver.getNumAccount())
                                                .balance(receiver.getBalance())
                                                .build());

                // Update transaction records for sender
                rabbitTemplate.convertAndSend(
                                RabbitMQConstants.TRANSACTION_UPDATE_EXCHANGE,
                                "",
                                new TransactionUpdateAccount(sender.getNumAccount(), sender.getBalance()));

                // Update transaction records for receiver
                rabbitTemplate.convertAndSend(
                                RabbitMQConstants.TRANSACTION_UPDATE_EXCHANGE,
                                "",
                                new TransactionUpdateAccount(receiver.getNumAccount(), receiver.getBalance()));
        }

        private void sendNotifications(Compte sender, Compte receiver, TransferRequest request) {
                // Sender notification
                rabbitTemplate.convertAndSend(
                                RabbitMQConstants.NOTIFICATION_EXCHANGE,
                                RabbitMQConstants.NOTIFICATION_TRANSACTION_KEY,
                                new NotificationRequest(
                                                null,
                                                sender.getUserEmail(),
                                                sender.getNumAgency(),
                                                "Transaction Réussie",
                                                "Transaction effectuée avec succès. Nouveau solde: "
                                                                + sender.getBalance(),
                                                new Date()));

                // Receiver notification
                rabbitTemplate.convertAndSend(
                                RabbitMQConstants.NOTIFICATION_EXCHANGE,
                                RabbitMQConstants.NOTIFICATION_TRANSACTION_KEY,
                                new NotificationRequest(
                                                null,
                                                receiver.getUserEmail(),
                                                receiver.getNumAgency(),
                                                "Transfert Reçu",
                                                "Vous avez reçu un transfert de: " + request.amount()
                                                                + ". Nouveau solde: " + receiver.getBalance(),
                                                new Date()));
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
