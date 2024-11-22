package com.m1fonda.service_agency.services;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.m1fonda.commons_libs.config.RabbitMQConstants;
import com.m1fonda.commons_libs.dto.AgencyDTO;
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
    public AgencyDTO updateAgencyInfo(AgencyDTO agency) {
        Agence agence = agencyRepository.findByNumAgency(agency.numAgency())
                .orElseThrow(() -> new RuntimeException("Agency not found"));
        agence.setName(Optional.ofNullable(agency.name()).orElse(agence.getName()));
        agence.setCapital(Optional.ofNullable(agency.capital()).orElse(agence.getCapital()));
        agence.setDepositBankRate(Optional.ofNullable(agency.depositBankRate()).orElse(agence.getDepositBankRate()));
        agence.setWithdrawalBankRate(
                Optional.ofNullable(agency.withdrawalBankRate()).orElse(agence.getWithdrawalBankRate()));
        agence.setAddress(Optional.ofNullable(agency.address()).orElse(agence.getAddress()));
        return AgencyDTO.fromAgency(agencyRepository.save(agence));
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
    public AgencyDTO createAgency(Agence agence) throws RuntimeException {
        return AgencyDTO.fromAgency(agencyRepository.save(agence));
    }

    public void agencyFallback(Object object, Throwable throwable) {
        System.out.println(
                "AGENCY SERVICE NOT WORKING: " + throwable.getMessage() + " caused by: " + throwable.getCause());
    }
}
