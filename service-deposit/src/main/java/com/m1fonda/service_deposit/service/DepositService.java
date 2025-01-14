package com.m1fonda.service_deposit.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.m1fonda.commons_libs.config.RabbitMQConstants;
import com.m1fonda.commons_libs.dto.AccountDTO;
import com.m1fonda.commons_libs.dto.AccountTransactionDTO;
import com.m1fonda.commons_libs.dto.AgencyUpdateTransaction;
import com.m1fonda.commons_libs.dto.NotificationRequest;
import com.m1fonda.commons_libs.dto.TransactionUpdateAccount;
import com.m1fonda.service_deposit.dto.AccountNotFoundException;
import com.m1fonda.service_deposit.dto.DepositRequest;
import com.m1fonda.service_deposit.dto.DepositResponse;
import com.m1fonda.service_deposit.dto.TransactionValidationException;
import com.m1fonda.service_deposit.model.Compte;
import com.m1fonda.service_deposit.model.Deposit;
import com.m1fonda.service_deposit.repository.CompteRepository;
import com.m1fonda.service_deposit.repository.DepositRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class DepositService {
    private final DepositRepository depositRepository;
    private final RabbitTemplate rabbitTemplate;
    private final CompteRepository compteRepository;

    @RabbitListener(queues = RabbitMQConstants.DEPOSIT_ACCOUNT_CREATION_QUEUE)
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

    @RabbitListener(queues = RabbitMQConstants.DEPOSIT_ACCOUNT_UPDATE_QUEUE)
    public void updateAccount(TransactionUpdateAccount account) {
        Compte compte = compteRepository.findByNumAccount(account.numAccount())
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + account.numAccount()));
        compte.setBalance(account.balance());
        compteRepository.save(compte);
    }

    @CircuitBreaker(name = "depositCircuitBreaker", fallbackMethod = "depositFallback")
    public DepositResponse newDeposit(DepositRequest request) {
        validateDepositRequest(request);

        String transactionID = getId();
        Compte compte = compteRepository.findByNumAccount(request.numAccount())
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + request.numAccount()));

        try {
            return executeDeposit(compte, request, transactionID);
        } catch (Exception e) {
            log.error("Error processing deposit: ", e);
            throw new TransactionValidationException("Failed to process deposit: " + e.getMessage());
        }
    }

    private void validateDepositRequest(DepositRequest request) {
        if (request.balance() <= 0) {
            throw new TransactionValidationException("Deposit amount must be greater than zero");
        }
        if (request.numAccount() == null || request.numAccount().trim().isEmpty()) {
            throw new TransactionValidationException("Account number is required");
        }
        if (request.numAgency() == null || request.numAgency().trim().isEmpty()) {
            throw new TransactionValidationException("Agency number is required");
        }
    }

    private DepositResponse executeDeposit(Compte compte, DepositRequest request, String transactionID) {
        double newBalance = compte.getBalance() + request.balance();
        compte.setBalance(newBalance);

        Deposit deposit = Deposit.builder()
                .amount(request.balance())
                .agencyNum(request.numAgency())
                .transactionNum(transactionID)
                .accountNum(request.numAccount())
                .createdAt(new Date())
                .build();

        sendDepositNotification(compte, newBalance);
        updateAccountBalances(compte, request);

        depositRepository.save(deposit);
        compteRepository.save(compte);

        return new DepositResponse(
                request.numAccount(),
                request.numAgency(),
                deposit.getTransactionNum(),
                compte.getBalance(),
                deposit.getCreatedAt());
    }

    private void sendDepositNotification(Compte compte, double newBalance) {
        rabbitTemplate.convertAndSend(
                RabbitMQConstants.NOTIFICATION_EXCHANGE,
                RabbitMQConstants.NOTIFICATION_TRANSACTION_KEY,
                new NotificationRequest(
                        null,
                        compte.getUserEmail(),
                        compte.getNumAgency(),
                        "Depot Réussie",
                        String.format("Dépôt effectué avec succès. Nouveau solde: %.2f", newBalance),
                        new Date()));
    }

    private void updateAccountBalances(Compte compte, DepositRequest request) {
        // Update account balance in other services
        rabbitTemplate.convertAndSend(
                RabbitMQConstants.ACCOUNT_EXCHANGE,
                RabbitMQConstants.ACCOUNT_UPDATE_KEY,
                AccountDTO.builder()
                        .numAccount(request.numAccount())
                        .balance(compte.getBalance())
                        .build());

        // Update transaction records
        rabbitTemplate.convertAndSend(
                RabbitMQConstants.TRANSACTION_UPDATE_EXCHANGE,
                "",
                new TransactionUpdateAccount(compte.getNumAccount(), compte.getBalance()));

        // Update agency records
        rabbitTemplate.convertAndSend(
                RabbitMQConstants.AGENCY_EXCHANGE,
                RabbitMQConstants.AGENCY_KEY,
                new AgencyUpdateTransaction(compte.getNumAgency(), request.balance()));
    }

    public List<DepositResponse> filterDeposits(String accountId, String agencyId) {
        validateFilterParameters(accountId, agencyId);

        if (accountId != null && agencyId != null) {
            return DepositResponse.fromList(
                    depositRepository.findByAgencyNumAndAccountNum(agencyId, accountId));
        }
        if (accountId != null) {
            return DepositResponse.fromList(
                    depositRepository.findByAccountNum(accountId));
        }
        return DepositResponse.fromList(
                depositRepository.findByAgencyNum(agencyId));
    }

    private void validateFilterParameters(String accountId, String agencyId) {
        if (accountId == null && agencyId == null) {
            throw new TransactionValidationException(
                    "At least one filter parameter (accountId or agencyId) must be provided");
        }
    }

    public List<DepositResponse> getAll() {
        return DepositResponse.fromList(depositRepository.findAll());
    }

    public void deleteAll() {
        depositRepository.deleteAll();
    }

    public String getId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }

    public void depositFallback(DepositRequest request, Throwable throwable) {
        String message = "Deposit service is unavailable: " + throwable.getMessage();
        log.error(message, throwable);
        throw new TransactionValidationException(message);
    }
}