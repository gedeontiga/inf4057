package com.m1fonda.service_account.services;

import java.util.Random;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.m1fonda.entities.Demand;
import com.m1fonda.service_account.entities.Compte;
import com.m1fonda.service_account.repositories.CompteRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CompteService {

    private final CompteRepository compteRepository;

    @CircuitBreaker(name = "serviceCompteCircuitBreaker", fallbackMethod = "creerCompteFallback")
    @RabbitListener(queues = "compteQueue")
    public void creerCompte(Demand demande) {
        if ("APPROVED".equals(demande.getStatus())) {

            Random random = new Random();
            Compte c = Compte.builder()
                    .numero("" + random.nextLong(999999999))
                    .solde(demande.getSolde())
                    .build();

            try {
                c = compteRepository.save(c);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void creerCompteFallback(Demand demand, Throwable throwable) {
        // Logique de repli en cas d'échec du Circuit Breaker
        System.out.println("Fallback - Demande a échoué : " + demand);
        System.out.println("Cause de l'échec : " + throwable.getMessage());
    }
}
