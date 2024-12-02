package com.m1fonda.service_account.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.m1fonda.commons_libs.config.RabbitMQConstants;
import com.m1fonda.commons_libs.dto.AccountDTO;
import com.m1fonda.commons_libs.dto.UserRequest;
import com.m1fonda.commons_libs.entities.Demand;
import com.m1fonda.commons_libs.entities.Status;
import com.m1fonda.service_account.entities.Compte;
import com.m1fonda.service_account.repositories.CompteRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CompteService {

    private static final String UPDATE_COMPTE_FALLBACK = "updateCompteFallback";
    private static final String CREER_COMPTE_FALLBACK = "creerCompteFallback";
    private static final String SERVICE_COMPTE_CIRCUIT_BREAKER = "serviceCompteCircuitBreaker";
    private final CompteRepository compteRepository;
    private final RabbitTemplate rabbitTemplate;

    @CircuitBreaker(name = SERVICE_COMPTE_CIRCUIT_BREAKER, fallbackMethod = CREER_COMPTE_FALLBACK)
    @RabbitListener(queues = RabbitMQConstants.ACCOUNT_CREATION_QUEUE)
    public Demand creerCompte(Demand demand) throws Exception {
        String numAgency = demand.getNumAgency();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String numAccount = uuid.substring(0, 8);
        Compte c = Compte.builder()
                .userEmail(demand.getEmail())
                .numAccount(numAccount)
                .balance(demand.getBalance())
                .status(Status.ACTIF)
                .createAt(new Date())
                .numAgency(numAgency)
                .build();

        rabbitTemplate.convertSendAndReceive(RabbitMQConstants.AUTH_EXCHANGE, RabbitMQConstants.AUTH_REGISTER_KEY,
                UserRequest.builder()
                        .cni(demand.getCni())
                        .email(demand.getEmail())
                        .lastName(demand.getLastName())
                        .phoneNumber(demand.getPhoneNumber())
                        .firstName(demand.getFirstName())
                        .password(demand.getPassword())
                        .build());
        compteRepository.save(c);
        demand.setStatus("APPROVED");
        return demand;
    }

    @CircuitBreaker(name = SERVICE_COMPTE_CIRCUIT_BREAKER, fallbackMethod = UPDATE_COMPTE_FALLBACK)
    @RabbitListener(queues = RabbitMQConstants.ACCOUNT_UPDATE_QUEUE)
    public AccountDTO updateAccount(AccountDTO account) {
        Compte c = compteRepository.findByNumAccount(account.numAccount())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        c.setBalance(Optional.ofNullable(c.getBalance() + account.balance()).orElse(c.getBalance()));
        c.setNumAgency(Optional.ofNullable(account.numAgency()).orElse(c.getNumAgency()));
        c.setStatus(Optional.ofNullable(account.status()).orElse(c.getStatus().name()));
        return AccountDTO.fromAccount(compteRepository.save(c));
    }

    public List<AccountDTO> getAccount(String email) {
        List<AccountDTO> result = new ArrayList<AccountDTO>();
        compteRepository.findByUserEmail(email).forEach(account -> result.add(AccountDTO.fromAccount(account)));
        return result;
    }

    @RabbitListener(queues = RabbitMQConstants.ACCOUNT_QUEUE)
    public long countClientByAgency(String numAgency) throws Exception {
        return compteRepository.countByNumAgency(numAgency);
    }

    public void creerCompteFallback(Demand demand, Throwable throwable) {
        // Logique de repli en cas d'échec du Circuit Breaker
        System.out.println("Fallback - Demande a échoué : " + demand.toString());
        System.out.println("Cause de l'échec : " + throwable.getMessage());
    }

    public void updateCompteFallback(AccountDTO accountDTO, Throwable throwable) {
        // Logique de repli en cas d'échec du Circuit Breaker
        System.out.println("Fallback - Demande a échoué : " + accountDTO.toString());
        System.out.println("Cause de l'échec : " + throwable.getMessage());
    }
}
