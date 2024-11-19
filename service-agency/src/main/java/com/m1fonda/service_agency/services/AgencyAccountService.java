package com.m1fonda.service_agency.services;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.m1fonda.commons_libs.config.RabbitMQConstants;
import com.m1fonda.service_agency.entities.Agence;
import com.m1fonda.service_agency.entities.Demande;
import com.m1fonda.service_agency.repositories.AgencyRepository;
import com.m1fonda.service_agency.repositories.DemandeRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AgencyAccountService {

    // private static final String RABBIT_LISTENER_CONTAINER_FACTORY =
    // "rabbitListenerContainerFactory";
    // private static final String PROCESS_DEMANDE_FALLBACK =
    // "processDemandeFallback";
    // private static final String SERVICE_BANQUE_CIRCUIT_BREAKER =
    // "serviceBanqueCircuitBreaker";
    private final RabbitTemplate rabbitTemplate;
    private final DemandeRepository demandeRepository;
    private final AgencyRepository agencyRepository;

    // @CircuitBreaker(name = SERVICE_BANQUE_CIRCUIT_BREAKER, fallbackMethod =
    // PROCESS_DEMANDE_FALLBACK)
    // @RabbitListener(queues = RabbitMQConstants.AGENCY_QUEUE, containerFactory =
    // RABBIT_LISTENER_CONTAINER_FACTORY)
    public Demande processDemande(Demande demande) {
        // boolean isApproved = validerDemande(demande);

        // if (isApproved) {
        demande.setStatus("APPROVED");
        rabbitTemplate.convertAndSend(RabbitMQConstants.ACCOUNT_EXCHANGE, RabbitMQConstants.ACCOUNT_CREATION_KEY,
                demande);

        Agence agence = agencyRepository.findByNumAgency(demande.getNumAgency()).orElseThrow();
        agence.setCapital(agence.getCapital() + demande.getBalance());
        agencyRepository.save(agence);
        return demandeRepository.save(demande);
        // System.out.println("Demande approuvée : " + demande);
        // } else {
        // demande.setStatus("REJECTED");
        // return demande;
        // rabbitTemplate.convertAndSend("demandeExchange",
        // "demande.reject.routing.key", demande);
        // System.out.println("Demande rejetée : " + demande);
        // }
    }

    // public void processDemandeFallback(Demande demand, Throwable throwable) {
    // // Logique de repli en cas d'échec du Circuit Breaker
    // System.out.println("Fallback - Demande a échoué : " + demand);
    // System.out.println("Cause de l'échec : " + throwable.getMessage());
    // }

    // private boolean validerDemande(Demande demande) {
    // return demande.getCni().length() == 11 &&
    // demande.getPhoneNumber().toString().length() == 9
    // && demande.getBalance() >= 50000;
    // }
}
