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
import com.m1fonda.commons_libs.dto.AccountRequestTransferDTO;
import com.m1fonda.commons_libs.dto.AccountResponseTransferDTO;
import com.m1fonda.commons_libs.dto.UserRequest;
import com.m1fonda.commons_libs.entities.Demand;
import com.m1fonda.commons_libs.entities.Status;
import com.m1fonda.service_account.dto.AccountUpdateDTO;
import com.m1fonda.service_account.entities.Compte;
import com.m1fonda.service_account.repositories.CompteRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.persistence.EntityNotFoundException;
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
    public void creerCompte(Demand demand) throws Exception {
        String numAgency = demand.getNumAgency();
        Integer uuid = (UUID.randomUUID().toString().replace("-", "")).hashCode();
        Integer numAccount = uuid > 0 ? uuid : -1 * uuid;

        Compte compte = Compte.builder()
                .userEmail(demand.getEmail())
                .numAccount(numAccount.toString())
                .balance(demand.getBalance())
                .status(Status.ACTIF)
                .createAt(new Date())
                .numAgency(numAgency)
                .numBank(demand.getNumBank())
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
        rabbitTemplate.convertAndSend(RabbitMQConstants.DEPOSIT_EXCHANGE,
                RabbitMQConstants.TRANSACTION_ACCOUNT_CREATION_KEY, compte);

        compteRepository.save(compte);
    }

    @CircuitBreaker(name = SERVICE_COMPTE_CIRCUIT_BREAKER, fallbackMethod = UPDATE_COMPTE_FALLBACK)
    @RabbitListener(queues = RabbitMQConstants.ACCOUNT_UPDATE_QUEUE)
    public AccountUpdateDTO updateAccount(AccountUpdateDTO account) {
        Compte compte = compteRepository.findByNumAccount(account.numAccount())
                .orElseThrow(() -> new RuntimeException("Account not found"));
        compte.setBalance(account.balance());
        compte.setStatus(Optional.ofNullable(account.status()).orElse(compte.getStatus().name()));
        return AccountUpdateDTO.fromAccount(compteRepository.save(compte));
    }

    public List<AccountDTO> getAccount(String email) {
        List<AccountDTO> result = new ArrayList<AccountDTO>();
        compteRepository.findByUserEmail(email).forEach(account -> result.add(AccountDTO.fromAccount(account)));
        return result;
    }

    public AccountResponseTransferDTO getTransferInfos(AccountRequestTransferDTO request) {
        Compte sender = compteRepository.findByNumAccount(request.numAccountSender()).orElseThrow();
        Compte receiver = compteRepository.findByNumAccount(request.numAccountReceiver()).orElseThrow();
        return new AccountResponseTransferDTO(sender.getNumBank(), receiver.getNumBank(), receiver.getUserEmail());
    }

    public long countClientByAgency(String numAgency) {
        return compteRepository.countByNumAgency(numAgency);
    }

    public long countClientByBank(String numBank) {
        return compteRepository.countByNumBank(numBank);
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

    public AccountDTO getAccountByAccountId(String numAccount) {
        return AccountDTO.fromAccount(compteRepository.findByNumAccount(numAccount)
                .orElseThrow(() -> new EntityNotFoundException("Account not found.")));
    }
}
