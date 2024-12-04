package com.m1fonda.service_bank.service;

import java.util.Set;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.m1fonda.commons_libs.config.RabbitMQConstants;
import com.m1fonda.commons_libs.dto.BankResponseDTO;
import com.m1fonda.commons_libs.dto.BankTransferDTO;
import com.m1fonda.service_bank.dto.AgencyDTO;
import com.m1fonda.service_bank.dto.BankDTO;
import com.m1fonda.service_bank.dto.BankDTOResponse;
import com.m1fonda.service_bank.repository.AgencyRepository;
import com.m1fonda.service_bank.repository.BankRepository;

import lombok.AllArgsConstructor;
import com.m1fonda.service_bank.dto.BankWithAgenciesDTO;
import com.m1fonda.service_bank.model.Agence;
import com.m1fonda.service_bank.model.Banque;

@Service
@AllArgsConstructor
public class BankService {

    private final BankRepository bankRepository;
    private final RabbitTemplate rabbitTemplate;
    private final AgencyRepository agencyRepository;

    // @RabbitListener(queues = "${rabbitmq.queue.name}", containerFactory =
    // "rabbitListenerContainerFactory")
    public BankDTOResponse createBank(BankDTO bank) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String bankNum = uuid.substring(0, 8);
        Banque banque = Banque.builder()
                .capital(bank.capital())
                .bankNumber(bankNum)
                .ownerEmail(bank.ownerEmail())
                .name(bank.name())
                .type(bank.type())
                .logo(bank.logo())
                .contact(bank.contact())
                .build();
        bankRepository.save(banque);
        return new BankDTOResponse(banque.getName(), banque.getLogo(), banque.getOwnerEmail(), banque.getType(),
                banque.getCapital(), banque.getContact(), banque.getBankNumber(), banque.getDateCreation());
    }

    public BankWithAgenciesDTO getBank(String id) {
        Banque bank = bankRepository.findByBankNumber(id).orElse(null);
        @SuppressWarnings("unchecked")
        Set<AgencyDTO> agencies = (Set<AgencyDTO>) rabbitTemplate
                .convertSendAndReceive(RabbitMQConstants.AGENCY_EXCHANGE, RabbitMQConstants.AGENCY_FIND_ALL_KEY, bank);
        return new BankWithAgenciesDTO(bank, agencies);
    }

    public BankWithAgenciesDTO addAgency(AgencyDTO agency) {
        rabbitTemplate.convertSendAndReceive(RabbitMQConstants.AGENCY_EXCHANGE, RabbitMQConstants.AGENCY_CREATION_KEY,
                agency);
        return getBank(agency.bankId());
    }

    public AgencyDTO updateAgency(AgencyDTO agency) {
        Agence agence = agencyRepository.findByNumAgency(agency.numAgency()).orElseThrow();
        agence.setAddress(agency.address());
        agence.setCapital(agency.capital());
        agence.setName(agency.name());
        rabbitTemplate.convertAndSend(RabbitMQConstants.AGENCY_EXCHANGE,
                RabbitMQConstants.AGENCY_UPDATE_KEY, agency);
        return agency;
    }

    public void removeAgency(AgencyDTO agency) {
        agencyRepository.deleteByNumAgency(agency.numAgency());
        rabbitTemplate.convertAndSend(RabbitMQConstants.AGENCY_EXCHANGE, RabbitMQConstants.AGENCY_DELETE_KEY,
                agency);
    }

    public BankResponseDTO checkBankAccountAndApplyFees(BankTransferDTO request) {
        Banque banque = bankRepository.findByBankNumber(request.numBankSender()).orElseThrow();
        if (request.numBankSender().equals(request.numBankReceiver())) {
            return new BankResponseDTO(banque.getName(), banque.getLogo(), banque.getInternalTransferBankFees(),
                    banque.getWithdrawalBankFees());
        } else {
            return new BankResponseDTO(banque.getName(), banque.getLogo(), banque.getExternalTransferBankFees(),
                    banque.getWithdrawalBankFees());
        }
    }

}
