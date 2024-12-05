package com.m1fonda.service_agency.services;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.m1fonda.commons_libs.config.RabbitMQConstants;
import com.m1fonda.commons_libs.dto.AgencyDTO;
import com.m1fonda.commons_libs.dto.AgencyUpdateTransaction;
import com.m1fonda.service_agency.entities.Agence;
import com.m1fonda.service_agency.repositories.AgencyRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AgencyService {

    private static final String AGENCY_CREATION_SERVICE = "AGENCY_CREATION_SERVICE";
    private static final String AGENCY_TRANSACTION_SERVICE = "AGENCY_TRANSACTION_SERVICE";
    private static final String AGENCY_DELETE_SERVICE = "AGENCY_DELETE_SERVICE";
    private static final String AGENCY_FIND_ALL_SERVICE = "AGENCY_FIND_ALL_SERVICE";
    private static final String AGENCY_UPDATE_SERVICE = "AGENCY_UPDATE_SERVICE";
    private static final String AGENCY_FALLBACK = "agencyFallback";
    private final RabbitTemplate rabbitTemplate;
    private AgencyRepository agencyRepository;

    public AgencyDTO getAgency(String numAgency) {
        return agencyRepository.findByNumAgency(numAgency)
                .map(AgencyDTO::fromAgency)
                .orElseThrow(() -> new EntityNotFoundException("Agency with number " + numAgency + " not found."));
    }

    @CircuitBreaker(name = AGENCY_UPDATE_SERVICE, fallbackMethod = AGENCY_FALLBACK)
    @RabbitListener(queues = RabbitMQConstants.AGENCY_UPDATE_QUEUE)
    public void updateAgencyInfo(AgencyDTO agency) {
        Agence agence = agencyRepository.findByNumAgency(agency.numAgency()).orElseThrow();
        agence.setName(Optional.ofNullable(agency.name()).orElse(agence.getName()));
        agence.setCapital(Optional.ofNullable(agency.capital()).orElse(agence.getCapital()));
        agence.setAddress(Optional.ofNullable(agency.address()).orElse(agence.getAddress()));
    }

    /*
     * Plus tard, faudra creer une méthode qui s'execute quotidiennement et met a
     * jour le capitale des agences
     */
    @CircuitBreaker(name = AGENCY_TRANSACTION_SERVICE, fallbackMethod = AGENCY_FALLBACK)
    @RabbitListener(queues = RabbitMQConstants.AGENCY_QUEUE)
    public void updateCapital(AgencyUpdateTransaction transaction) {
        Agence agence = agencyRepository.findByNumAgency(transaction.numAgency()).orElseThrow();
        double amount = transaction.amountTransaction();
        if (agence.getCapital() < amount * 50) {
            amount = (double) rabbitTemplate.convertSendAndReceive(RabbitMQConstants.BANK_EXCHANGE,
                    RabbitMQConstants.BANK_KEY, amount * 2);
        }
        agence.setCapital(agence.getCapital() + amount);
    }

    @CircuitBreaker(name = AGENCY_FIND_ALL_SERVICE, fallbackMethod = AGENCY_FALLBACK)
    @RabbitListener(queues = RabbitMQConstants.AGENCY_FIND_ALL_QUEUE)
    public Set<AgencyDTO> getAllAgencies(String numBank) {
        Set<AgencyDTO> allAgencies = new HashSet<>();
        agencyRepository.findAllByNumBank(numBank).forEach(agency -> allAgencies.add(AgencyDTO.fromAgency(agency)));
        return allAgencies;
    }

    @CircuitBreaker(name = AGENCY_DELETE_SERVICE, fallbackMethod = AGENCY_FALLBACK)
    @RabbitListener(queues = RabbitMQConstants.AGENCY_DELETE_QUEUE)
    public void deleteAgency(AgencyDTO agency) {
        agencyRepository.deleteByNumAgency(agency.numAgency());
    }

    @CircuitBreaker(name = AGENCY_CREATION_SERVICE, fallbackMethod = AGENCY_FALLBACK)
    @RabbitListener(queues = RabbitMQConstants.AGENCY_CREATION_QUEUE)
    public void createAgency(AgencyDTO agence) throws Exception {
        agencyRepository.save(
                Agence.builder()
                        .capital(agence.capital())
                        .address(agence.address())
                        .name(agence.name())
                        .numAgency(agence.numAgency())
                        .numBank(agence.numBank())
                        .build());
    }

    public long getClientNumber(String numAgency) throws Exception {
        return (long) rabbitTemplate.convertSendAndReceive(RabbitMQConstants.ACCOUNT_EXCHANGE,
                RabbitMQConstants.ACCOUNT_KEY, numAgency);
    }

    public void agencyFallback(Object object, Throwable throwable) {
        System.out.println(
                "AGENCY SERVICE NOT WORKING: " + throwable.getMessage() + " caused by: " + throwable.getCause());
    }
}
