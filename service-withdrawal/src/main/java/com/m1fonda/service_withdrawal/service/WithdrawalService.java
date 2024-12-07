package com.m1fonda.service_withdrawal.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.m1fonda.service_withdrawal.dto.WithdrawalRequest;
import com.m1fonda.service_withdrawal.dto.WithdrawalResponse;
import com.m1fonda.service_withdrawal.model.Withdrawal;
import com.m1fonda.service_withdrawal.repository.WithdrawalRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.m1fonda.commons_libs.config.RabbitMQConstants;
import com.m1fonda.commons_libs.dto.AccountDTO;

@Slf4j
@Service
@AllArgsConstructor
@CircuitBreaker(name = "withdrawalCircuitBreaker", fallbackMethod = "withdrawalFallback")
public class WithdrawalService {

    private final WithdrawalRepository withdrawalRepository;
    public final RabbitTemplate rabbitTemplate;

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

        withdrawalRepository.save(withdrawal);

        rabbitTemplate.convertAndSend(RabbitMQConstants.ACCOUNT_EXCHANGE, RabbitMQConstants.ACCOUNT_UPDATE_KEY,
                AccountDTO.builder()
                        .numAccount(request.accountNum())
                        .numAgency(request.agencyNum())
                        .balance(-1 * (request.amount()))
                        .fees(request.fees())
                        .build());

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
