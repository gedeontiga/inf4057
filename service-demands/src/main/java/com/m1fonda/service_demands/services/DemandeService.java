package com.m1fonda.service_demands.services;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.m1fonda.entities.Demand;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class DemandeService {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitAdmin rabbitAdmin;

    public DemandeService(RabbitTemplate rabbitTemplate, RabbitAdmin rabbitAdmin) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitAdmin = rabbitAdmin;
    }

    @CircuitBreaker(name = "serviceDemandeCircuitBreaker", fallbackMethod = "demandeFallback")
    public void envoyerDemande(Demand demande) {
        demande.setStatus("PENDING");
        rabbitTemplate.convertAndSend("demandeExchange", "agency.routing.key", demande);
    }

    public void demandeFallback(Demand demand, Throwable throwable) {
        // Logique de repli en cas d'échec du Circuit Breaker
        System.out.println("Fallback - Demande a échoué : " + demand);
        System.out.println("Cause de l'échec : " + throwable.getMessage());
    }

    @Scheduled(fixedRate = 60000)
    @CircuitBreaker(name = "serviceDemandeRejeteCircuitBreaker", fallbackMethod = "rejeteDemandeFallback")
    @RabbitListener(queues = "demandeQueue", containerFactory = "rabbitListenerContainerFactory")
    public void cleanQueue() {
        rabbitAdmin.purgeQueue("demandeQueue");
        System.out.println("Queue demandes vidée");
    }

    public void rejeteDemandeFallback(Demand demand, Throwable throwable) {
        // Logique de repli en cas d'échec du Circuit Breaker
        System.out.println("Fallback - Demande a échoué : " + demand);
        System.out.println("Cause de l'échec : " + throwable.getMessage());
    }
}
