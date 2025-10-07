package com.m1fonda.service_agency.services;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.stereotype.Service;

import com.m1fonda.commons_libs.config.RabbitMQConstants;
import com.m1fonda.commons_libs.dto.AgencyDTO;
import com.m1fonda.commons_libs.dto.AgencyUpdateTransaction;
import com.m1fonda.commons_libs.dto.ManagerRequestDTO;
import com.m1fonda.service_agency.dto.ManagerDTO;
import com.m1fonda.service_agency.entities.Agence;
import com.m1fonda.service_agency.entities.Managers;
import com.m1fonda.service_agency.repositories.AgencyRepository;
import com.m1fonda.service_agency.repositories.ManagersRepository;

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
    private final AgencyRepository agencyRepository;
    private final ManagersRepository managersRepository;

    public AgencyDTO getAgency(String numAgency) {
        return agencyRepository.findByNumAgency(numAgency)
                .map(AgencyDTO::fromAgency)
                .orElseThrow(() -> new EntityNotFoundException("Agency with number " + numAgency + " not found."));
    }

    @CircuitBreaker(name = AGENCY_UPDATE_SERVICE, fallbackMethod = AGENCY_FALLBACK)
    @RabbitListener(queues = RabbitMQConstants.AGENCY_UPDATE_QUEUE)
    public AgencyDTO updateAgencyInfo(AgencyDTO agency) {
        Agence agence = agencyRepository.findByNumAgency(agency.numAgency()).orElseThrow();
        agence.setName(Optional.ofNullable(agency.name()).orElse(agence.getName()));
        agence.setCapital(Optional.ofNullable(agency.capital()).orElse(agence.getCapital()));
        agence.setAddress(Optional.ofNullable(agency.address()).orElse(agence.getAddress()));
        return AgencyDTO.fromAgency(agencyRepository.save(agence));
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(RabbitMQConstants.MANAGER_CREATION_QUEUE), exchange = @Exchange(value = RabbitMQConstants.AGENCY_EXCHANGE, type = ExchangeTypes.DIRECT), key = RabbitMQConstants.MANAGER_CREATION_KEY))
    public void createManager(ManagerRequestDTO agent) {
        agencyRepository.findByNumAgency(agent.numAgency()).orElseThrow(
                () -> new EntityNotFoundException("Agency with number " + agent.numAgency() + " not found."));
        if (managersRepository.findByEmail(agent.email()).isPresent())
            throw new EntityNotFoundException("Agency with email " + agent.email() + "already exists.");
        Managers manager = Managers.builder()
                .email(agent.email())
                .numAgency(agent.numAgency())
                .numCni(agent.numCni())
                .build();
        managersRepository.save(manager);
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue(RabbitMQConstants.MANAGER_DELETION_QUEUE), exchange = @Exchange(value = RabbitMQConstants.AGENCY_EXCHANGE, type = ExchangeTypes.DIRECT), key = RabbitMQConstants.MANAGER_DELETION_KEY))
    public void deleteManager(String email) {
        Managers manager = managersRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException("Manager mot found."));
        managersRepository.deleteById(manager.getId());
    }

    @CircuitBreaker(name = AGENCY_TRANSACTION_SERVICE, fallbackMethod = AGENCY_FALLBACK)
    @RabbitListener(queues = RabbitMQConstants.AGENCY_QUEUE)
    public void updateCapital(AgencyUpdateTransaction transaction) {
        Agence agence = agencyRepository.findByNumAgency(transaction.numAgency()).orElseThrow(
                () -> new EntityNotFoundException("Agency not found."));
        agence.setCapital(agence.getCapital() + transaction.amountTransaction());
        agencyRepository.save(agence);
    }

    @CircuitBreaker(name = AGENCY_FIND_ALL_SERVICE, fallbackMethod = AGENCY_FALLBACK)
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

    public void agencyFallback(Object object, Throwable throwable) {
        System.out.println(
                "AGENCY SERVICE NOT WORKING: " + throwable.getMessage() + " caused by: " + throwable.getCause());
    }

    public Set<ManagerDTO> getAllManagers(String numAgency) {
        Set<ManagerDTO> managers = new HashSet<ManagerDTO>();
        managersRepository.findByNumAgency(numAgency)
                .forEach(manager -> managers.add(ManagerDTO.managerFactory(manager)));
        return managers;
    }
}
