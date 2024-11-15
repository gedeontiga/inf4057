package com.m1fonda.service_account.services;

import java.util.Random;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.m1fonda.config.RabbitMQConstants;
import com.m1fonda.entities.Demand;
import com.m1fonda.service_account.entities.Compte;
import com.m1fonda.service_account.repositories.CompteRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CompteService {

    private static final String CREER_COMPTE_FALLBACK = "creerCompteFallback";
    private static final String SERVICE_COMPTE_CIRCUIT_BREAKER = "serviceCompteCircuitBreaker";
    private final CompteRepository compteRepository;
    private final RabbitTemplate rabbitTemplate;

    @CircuitBreaker(name = SERVICE_COMPTE_CIRCUIT_BREAKER, fallbackMethod = CREER_COMPTE_FALLBACK)
    @RabbitListener(queues = RabbitMQConstants.ACCOUNT_CREATION_QUEUE)
    public void creerCompte(Demand demand) {
        if ("APPROVED".equals(demand.getStatus())) {
            String numAgency = demand.getNumAgency();

            Random random = new Random();
            Compte c = Compte.builder()
                    .accountNumber(
                            String.format("", numAgency.toCharArray()[3]) + random.nextLong(999999))
                    .balance(demand.getBalance())
                    .numAgency(numAgency)
                    .build();

            rabbitTemplate.convertAndSend(RabbitMQConstants.USER_EXCHANGE, RabbitMQConstants.USER_REGISTRATION_KEY,
                    demand);

            try {
                c = compteRepository.save(c);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void creerCompteFallback(Demand demand, Throwable throwable) {
        // Logique de repli en cas d'échec du Circuit Breaker
        System.out.println("Fallback - Demande a échoué : " + demand.toString());
        System.out.println("Cause de l'échec : " + throwable.getMessage());
    }
}
