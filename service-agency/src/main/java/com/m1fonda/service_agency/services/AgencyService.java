package com.m1fonda.service_agency.services;

import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.m1fonda.config.RabbitMQConstants;
import com.m1fonda.service_agency.entities.Agence;
import com.m1fonda.service_agency.repositories.AgencyRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AgencyService {

    private static final String AGENCY_CREATION_SERVICE = "AGENCY_CREATION_SERVICE";
    private static final String AGENCY_DELETE_SERVICE = "AGENCY_DELETE_SERVICE";
    private static final String AGENCY_FIND_ALL_SERVICE = "AGENCY_FIND_ALL_SERVICE";
    private static final String AGENCY_UPDATE_SERVICE = "AGENCY_UPDATE_SERVICE";
    private static final String AGENCY_FALLBACK = "agencyFallback";
    private AgencyRepository agencyRepository;

    public Agence getAgency(Long code) {
        return agencyRepository.findById(code).orElseThrow();
    }

    @CircuitBreaker(name = AGENCY_UPDATE_SERVICE, fallbackMethod = AGENCY_FALLBACK)
    @RabbitListener(queues = RabbitMQConstants.AGENCY_UPDATE_QUEUE)
    public Agence updateAgencyInfo(Agence agence) {
        return agencyRepository.save(agence);
    }

    @CircuitBreaker(name = AGENCY_FIND_ALL_SERVICE, fallbackMethod = AGENCY_FALLBACK)
    @RabbitListener(queues = RabbitMQConstants.AGENCY_FIND_ALL_QUEUE)
    public List<Agence> getAllAgencies() {
        return agencyRepository.findAll();
    }

    @CircuitBreaker(name = AGENCY_DELETE_SERVICE, fallbackMethod = AGENCY_FALLBACK)
    @RabbitListener(queues = RabbitMQConstants.AGENCY_DELETE_QUEUE)
    public void deleteAgency(Long code) {
        agencyRepository.deleteById(code);
    }

    @CircuitBreaker(name = AGENCY_CREATION_SERVICE, fallbackMethod = AGENCY_FALLBACK)
    @RabbitListener(queues = RabbitMQConstants.AGENCY_CREATION_QUEUE)
    public Agence createAgency(Agence agence) {
        return agencyRepository.save(agence);
    }

    public void agencyFallback() {
        System.out.println("AGENCY SERVICE NOT WORKING");
    }
}
