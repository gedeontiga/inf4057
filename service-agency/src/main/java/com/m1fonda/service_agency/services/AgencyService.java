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
import com.m1fonda.commons_libs.dto.DepositRequest;
import com.m1fonda.commons_libs.dto.DepositResponse;
import com.m1fonda.commons_libs.dto.TransferRequest;
import com.m1fonda.commons_libs.dto.TransferResponse;
import com.m1fonda.commons_libs.dto.WithdrawalRequest;
import com.m1fonda.commons_libs.dto.WithdrawalResponse;
import com.m1fonda.service_agency.entities.Agence;
import com.m1fonda.service_agency.repositories.AgencyRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
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

    public AgencyDTO getAgency(String numAgency) throws Exception {
        return AgencyDTO.fromAgency(agencyRepository.findByNumAgency(numAgency));
    }

    @CircuitBreaker(name = AGENCY_UPDATE_SERVICE, fallbackMethod = AGENCY_FALLBACK)
    @RabbitListener(queues = RabbitMQConstants.AGENCY_UPDATE_QUEUE)
    public AgencyDTO updateAgencyInfo(AgencyDTO agency) {
        Agence agence = agencyRepository.findByNumAgency(agency.numAgency());
        agence.setName(Optional.ofNullable(agency.name()).orElse(agence.getName()));
        agence.setCapital(Optional.ofNullable(agency.capital()).orElse(agence.getCapital()));
        agence.setDepositBankRate(Optional.ofNullable(agency.depositBankRate()).orElse(agence.getDepositBankRate()));
        agence.setWithdrawalBankRate(
                Optional.ofNullable(agency.withdrawalBankRate()).orElse(agence.getWithdrawalBankRate()));
        agence.setAddress(Optional.ofNullable(agency.address()).orElse(agence.getAddress()));
        return AgencyDTO.fromAgency(agencyRepository.save(agence));
    }

    /*
     * Plus tard, faudra creer une méthode qui s'execute quotidiennement et met a
     * jour le capitale des agences
     */
    @CircuitBreaker(name = AGENCY_TRANSACTION_SERVICE, fallbackMethod = AGENCY_FALLBACK)
    @RabbitListener(queues = RabbitMQConstants.AGENCY_QUEUE)
    public void updateCapital(AgencyUpdateTransaction transaction) throws Exception {
        Agence agence = agencyRepository.findByNumAgency(transaction.numAgency());
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
    public AgencyDTO createAgency(Agence agence) throws Exception {
        return AgencyDTO.fromAgency(agencyRepository.save(agence));
    }

    public long getClientNumber(String numAgency) throws Exception {
        return (long) rabbitTemplate.convertSendAndReceive(RabbitMQConstants.ACCOUNT_EXCHANGE,
                RabbitMQConstants.ACCOUNT_KEY, numAgency);
    }

    public DepositResponse sendDepositRequest(DepositRequest request) {
        return (DepositResponse) rabbitTemplate.convertSendAndReceive(RabbitMQConstants.DEPOSIT_EXCHANGE,
                RabbitMQConstants.DEPOSIT_KEY, request);
    }

    public WithdrawalResponse sendWithDrawalRequest(WithdrawalRequest request) {
        return (WithdrawalResponse) rabbitTemplate.convertSendAndReceive(RabbitMQConstants.WITHDRAW_EXCHANGE,
                RabbitMQConstants.WITHDRAW_KEY, request);
    }

    public TransferResponse sendTransferRequest(TransferRequest request) {
        return (TransferResponse) rabbitTemplate.convertSendAndReceive(RabbitMQConstants.TRANSFER_EXCHANGE,
                RabbitMQConstants.TRANSFER_KEY, request);
    }

    public void agencyFallback(Object object, Throwable throwable) {
        System.out.println(
                "AGENCY SERVICE NOT WORKING: " + throwable.getMessage() + " caused by: " + throwable.getCause());
    }
}
