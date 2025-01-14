package com.m1fonda.service_withdrawal.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.m1fonda.service_withdrawal.dto.AccountNotFoundException;
import com.m1fonda.service_withdrawal.dto.InsufficientBalanceException;
import com.m1fonda.service_withdrawal.dto.TransactionValidationException;
import com.m1fonda.service_withdrawal.dto.WithdrawalRequest;
import com.m1fonda.service_withdrawal.dto.WithdrawalResponse;
import com.m1fonda.service_withdrawal.model.Compte;
import com.m1fonda.service_withdrawal.model.Withdrawal;
import com.m1fonda.service_withdrawal.repository.CompteRepository;
import com.m1fonda.service_withdrawal.repository.WithdrawalRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.m1fonda.commons_libs.config.RabbitMQConstants;
import com.m1fonda.commons_libs.dto.AccountDTO;
import com.m1fonda.commons_libs.dto.AccountTransactionDTO;
import com.m1fonda.commons_libs.dto.AgencyUpdateTransaction;
import com.m1fonda.commons_libs.dto.NotificationRequest;
import com.m1fonda.commons_libs.dto.TransactionUpdateAccount;

@Slf4j
@Service
@AllArgsConstructor
public class WithdrawalService {
    private final WithdrawalRepository withdrawalRepository;
    private final RabbitTemplate rabbitTemplate;
    private final CompteRepository compteRepository;

    @RabbitListener(queues = RabbitMQConstants.WITHDRAWAL_ACCOUNT_CREATION_QUEUE)
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

    @RabbitListener(queues = RabbitMQConstants.WITHDRAWAL_ACCOUNT_UPDATE_QUEUE)
    public void updateAccount(TransactionUpdateAccount account) {
        Compte compte = compteRepository.findByNumAccount(account.numAccount())
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + account.numAccount()));
        compte.setBalance(account.balance());
        compteRepository.save(compte);
    }

    @CircuitBreaker(name = "withdrawalCircuitBreaker", fallbackMethod = "withdrawalFallback")
    public WithdrawalResponse newWithdrawal(WithdrawalRequest request) {
        validateWithdrawalRequest(request);

        String transactionID = getId();
        Compte account = compteRepository.findByNumAccount(request.accountNum())
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + request.accountNum()));

        double totalAmount = calculateTotalAmount(request);
        double newBalance = account.getBalance() - totalAmount;

        if (newBalance < 0) {
            throw new InsufficientBalanceException(String.format(
                    "Insufficient balance for withdrawal. Required: %.2f, Available: %.2f",
                    totalAmount, account.getBalance()));
        }

        try {
            return executeWithdrawal(account, request, newBalance, transactionID);
        } catch (Exception e) {
            log.error("Error processing withdrawal: ", e);
            throw new TransactionValidationException("Failed to process withdrawal: " + e.getMessage());
        }
    }

    private void validateWithdrawalRequest(WithdrawalRequest request) {
        if (request.amount() <= 0) {
            throw new TransactionValidationException("Withdrawal amount must be greater than zero");
        }
        if (request.fees() < 0) {
            throw new TransactionValidationException("Fees cannot be negative");
        }
        if (request.accountNum() == null || request.accountNum().trim().isEmpty()) {
            throw new TransactionValidationException("Account number is required");
        }
        if (request.agencyNum() == null || request.agencyNum().trim().isEmpty()) {
            throw new TransactionValidationException("Agency number is required");
        }
    }

    private double calculateTotalAmount(WithdrawalRequest request) {
        return request.amount() + (request.amount() * request.fees());
    }

    private WithdrawalResponse executeWithdrawal(Compte account, WithdrawalRequest request,
            double newBalance, String transactionID) {
        account.setBalance(newBalance);

        Withdrawal withdrawal = Withdrawal.builder()
                .amount(request.amount())
                .transactionNum(transactionID)
                .accountNum(request.accountNum())
                .agencyNum(request.agencyNum())
                .fees(request.fees())
                .createdAt(new Date())
                .build();

        sendWithdrawalNotification(account, newBalance);
        updateAccountBalances(account, request);

        compteRepository.save(account);
        withdrawalRepository.save(withdrawal);

        return WithdrawalResponse.fromWithdrawal(withdrawal);
    }

    private void sendWithdrawalNotification(Compte account, double newBalance) {
        rabbitTemplate.convertAndSend(
                RabbitMQConstants.NOTIFICATION_EXCHANGE,
                RabbitMQConstants.NOTIFICATION_TRANSACTION_KEY,
                new NotificationRequest(
                        null,
                        account.getUserEmail(),
                        account.getNumAgency(),
                        "Retrait Réussie",
                        String.format("Retrait effectué avec succès. Nouveau solde: %.2f", newBalance),
                        new Date()));
    }

    private void updateAccountBalances(Compte account, WithdrawalRequest request) {
        // Update account balance in other services
        rabbitTemplate.convertAndSend(
                RabbitMQConstants.ACCOUNT_EXCHANGE,
                RabbitMQConstants.ACCOUNT_UPDATE_KEY,
                AccountDTO.builder()
                        .numAccount(request.accountNum())
                        .balance(account.getBalance())
                        .build());

        // Update transaction records
        rabbitTemplate.convertAndSend(
                RabbitMQConstants.TRANSACTION_UPDATE_EXCHANGE,
                "",
                new TransactionUpdateAccount(account.getNumAccount(), account.getBalance()));

        // Update agency records
        rabbitTemplate.convertAndSend(
                RabbitMQConstants.AGENCY_EXCHANGE,
                RabbitMQConstants.AGENCY_KEY,
                new AgencyUpdateTransaction(account.getNumAgency(), -1 * request.amount()));
    }

    public List<WithdrawalResponse> filterWithdrawals(String accountId, String agencyId) {
        validateFilterParameters(accountId, agencyId);

        if (accountId != null && agencyId != null) {
            return WithdrawalResponse.fromList(
                    withdrawalRepository.findByAgencyNumAndAccountNum(agencyId, accountId));
        }
        if (accountId != null) {
            return WithdrawalResponse.fromList(
                    withdrawalRepository.findByAccountNum(accountId));
        }
        return WithdrawalResponse.fromList(
                withdrawalRepository.findByAgencyNum(agencyId));
    }

    private void validateFilterParameters(String accountId, String agencyId) {
        if (accountId == null && agencyId == null) {
            throw new TransactionValidationException(
                    "At least one filter parameter (accountId or agencyId) must be provided");
        }
    }

    public List<WithdrawalResponse> getAll() {
        return WithdrawalResponse.fromList(withdrawalRepository.findAll());
    }

    public void deleteAll() {
        withdrawalRepository.deleteAll();
    }

    public String getId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }

    public void withdrawalFallback(WithdrawalRequest request, Throwable throwable) {
        String message = "Withdrawal service is unavailable: " + throwable.getMessage();
        log.error(message, throwable);
        throw new TransactionValidationException(message);
    }
}