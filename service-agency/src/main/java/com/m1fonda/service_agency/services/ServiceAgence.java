package com.m1fonda.service_agency.services;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.m1fonda.entities.Demand;
import com.m1fonda.service_agency.entities.Demande;
import com.m1fonda.service_agency.repositories.DemandeRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ServiceAgence {

    private final RabbitTemplate rabbitTemplate;
    private final DemandeRepository demandeRepository;

    @CircuitBreaker(name = "serviceBanqueCircuitBreaker", fallbackMethod = "processDemandeFallback")
    @RabbitListener(queues = "agencyQueue", containerFactory = "rabbitListenerContainerFactory")
    public void processDemande(Demand demande) {
        boolean isApproved = validerDemande(demande);

        if (isApproved) {
            demande.setStatus("APPROVED");
            rabbitTemplate.convertAndSend("compteExchange", "compte.routing.key", demande);
            demandeRepository.save((Demande) demande);
            System.out.println("Demande approuvée : " + demande);
        } else {
            demande.setStatus("REJECTED");
            rabbitTemplate.convertAndSend("demandeExchange", "demande.reject.routing.key", demande);
            System.out.println("Demande rejetée : " + demande);
        }
    }

    public void processDemandeFallback(Demand demand, Throwable throwable) {
        // Logique de repli en cas d'échec du Circuit Breaker
        System.out.println("Fallback - Demande a échoué : " + demand);
        System.out.println("Cause de l'échec : " + throwable.getMessage());
    }

    private boolean validerDemande(Demand demande) {
        return demande.getCni().length() == 11 && demande.getTel().toString().length() == 9
                && demande.getSolde() >= 50000;
    }
}
