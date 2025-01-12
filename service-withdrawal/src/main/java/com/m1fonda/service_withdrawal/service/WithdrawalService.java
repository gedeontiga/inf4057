package com.m1fonda.service_withdrawal.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.m1fonda.service_withdrawal.dto.WithdrawalRequest;
import com.m1fonda.service_withdrawal.dto.WithdrawalResponse;
import com.m1fonda.service_withdrawal.model.Compte;
import com.m1fonda.service_withdrawal.model.Withdrawal;
import com.m1fonda.service_withdrawal.repository.CompteRepository;
import com.m1fonda.service_withdrawal.repository.WithdrawalRepository;

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
@CircuitBreaker(name = "withdrawalCircuitBreaker", fallbackMethod = "withdrawalFallback")
public class WithdrawalService {

    private final WithdrawalRepository withdrawalRepository;
    public final RabbitTemplate rabbitTemplate;
    private final CompteRepository compteRepository;

    @RabbitListener(queues = RabbitMQConstants.WITHDRAWAL_ACCOUNT_CREATION_QUEUE)
    public void createAccount(Compte account) {
        Compte compte = Compte.builder()
                .userEmail(account.getUserEmail())
                .numAccount(account.getNumAccount())
                .balance(account.getBalance())
                .numAgency(account.getNumAgency())
                .build();
        compteRepository.save(compte);
    }

    @CircuitBreaker(name = "withdrawalCircuitBreaker", fallbackMethod = "withdrawalFallback")
    public WithdrawalResponse newWithdrawal(WithdrawalRequest request) {

        String transactionID = getId();

        Withdrawal withdrawal = Withdrawal.builder()
                .amount(request.amount())
                .transactionNum(transactionID)
                .accountNum(request.accountNum())
                .agencyNum(request.agencyNum())
                .fees(request.fees())
                .createdAt(new Date())
                .build();

        Compte account = compteRepository.findByNumAccount(request.accountNum())
                .orElseThrow(() -> new EntityNotFoundException("Reciever account not found"));
        double newBalance = account.getBalance() - (request.amount() + request.amount() * request.fees());

        if (newBalance >= 0) {
            account.setBalance(newBalance);
            rabbitTemplate.convertAndSend(RabbitMQConstants.NOTIFICATION_EXCHANGE,
                    RabbitMQConstants.NOTIFICATION_TRANSACTION_KEY,
                    new NotificationRequest(null, account.getUserEmail(),
                            account.getNumAgency(),
                            "Retrait Réussie",
                            "Retrait effectuée avec succès, nouveau solde : " + account.getBalance(), new Date()));

            rabbitTemplate.convertAndSend(RabbitMQConstants.ACCOUNT_EXCHANGE,
                    RabbitMQConstants.ACCOUNT_UPDATE_KEY,
                    AccountDTO.builder().numAccount(request.accountNum())
                            .balance(account.getBalance()).build());

            rabbitTemplate.convertAndSend(RabbitMQConstants.AGENCY_EXCHANGE, RabbitMQConstants.AGENCY_KEY,
                    new AgencyUpdateTransaction(account.getNumAgency(),
                            -1 * request.amount()));
        } else {
            rabbitTemplate.convertAndSend(RabbitMQConstants.NOTIFICATION_EXCHANGE,
                    RabbitMQConstants.NOTIFICATION_TRANSACTION_KEY,
                    new NotificationRequest(null, account.getUserEmail(),
                            account.getNumAgency(),
                            "Retrait Échouée",
                            "Désolé, votre solde est insuffisant pour effectuer cette transaction.",
                            new Date()));
            throw new IllegalArgumentException("Balance Insuficient");
        }

        compteRepository.save(account);

        withdrawalRepository.save(withdrawal);

        return WithdrawalResponse.fromWithdrawal(withdrawal);
    }

    public List<WithdrawalResponse> getAll() {
        return WithdrawalResponse.fromList(withdrawalRepository.findAll());
    }

    public void deleteAll() {
        withdrawalRepository.deleteAll();
    }

    public List<WithdrawalResponse> filterWithdrawals(String accountId, String agencyId) {
        if (accountId != null && agencyId != null)
            return WithdrawalResponse.fromList(withdrawalRepository.findByAgencyNumAndAccountNum(agencyId, accountId));
        if (accountId != null)
            return WithdrawalResponse.fromList(withdrawalRepository.findByAccountNum(accountId));
        return WithdrawalResponse.fromList(withdrawalRepository.findByAgencyNum(agencyId));
    }

    public String getId() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid.substring(0, 10);
    }

    public void withdrawalFallback() {
        String message = "Withdrawal service is latent or down...";
        System.out.println(message);
        log.info(message);
    }
}
