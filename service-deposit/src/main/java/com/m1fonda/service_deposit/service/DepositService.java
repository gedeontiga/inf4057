package com.m1fonda.service_deposit.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.m1fonda.commons_libs.config.RabbitMQConstants;
import com.m1fonda.commons_libs.dto.AccountDTO;
import com.m1fonda.service_deposit.dto.DepositRequest;
import com.m1fonda.service_deposit.dto.DepositResponse;
import com.m1fonda.service_deposit.model.Deposit;
import com.m1fonda.service_deposit.repository.DepositRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class DepositService {

    private final DepositRepository depositRepository;
    public final RabbitTemplate rabbitTemplate;

    @CircuitBreaker(name = "depositCircuitBreaker", fallbackMethod = "depositFallback")
    public DepositResponse newDeposit(DepositRequest request) {

        String transactionID = getId();

        Deposit deposit = Deposit.builder()
                .amount(request.balance())
                .agencyNum(request.numAgency())
                .transactionNum(transactionID)
                .accountNum(request.numAccount())
                .createdAt(new Date())
                .build();

        depositRepository.save(deposit);

        rabbitTemplate.convertAndSend(RabbitMQConstants.ACCOUNT_EXCHANGE, RabbitMQConstants.ACCOUNT_UPDATE_KEY,
                AccountDTO.builder()
                        .numAccount(request.numAccount())
                        .balance(request.balance())
                        .build());

        return new DepositResponse(request.numAccount(), request.numAgency(), deposit.getTransactionNum(),
                deposit.getAmount(), deposit.getCreatedAt());
    }

    public List<DepositResponse> getAll() {
        return DepositResponse.fromList(depositRepository.findAll());
    }

    public void deleteAll() {
        depositRepository.deleteAll();
    }

    public String getId() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid.substring(0, 10);
    }

    public List<DepositResponse> filterDeposits(String accountId, String agencyId) {
        if (accountId != null && agencyId != null)
            return DepositResponse.fromList(depositRepository.findByAgencyNumAndAccountNum(agencyId, accountId));
        if (accountId != null)
            return DepositResponse.fromList(depositRepository.findByAccountNum(accountId));
        return DepositResponse.fromList(depositRepository.findByAgencyNum(agencyId));
    }

    public void depositFallback() {
        String message = "Deposit service is latent or down...";
        System.out.println(message);
        log.info(message);
    }

}
