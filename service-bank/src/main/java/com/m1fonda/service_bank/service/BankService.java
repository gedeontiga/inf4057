package com.m1fonda.service_bank.service;

import java.util.Set;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.m1fonda.commons_libs.config.*;
import com.m1fonda.service_bank.dto.AgencyDTO;
import com.m1fonda.service_bank.dto.BankDTO;
import com.m1fonda.service_bank.dto.BankDTOResponse;
import com.m1fonda.service_bank.model.BankModel;
import com.m1fonda.service_bank.repository.BankRepository;

import lombok.AllArgsConstructor;
import com.m1fonda.service_bank.dto.BankWithAgenciesDTO;

@Component
@Service
@AllArgsConstructor
public class BankService {

    private final BankRepository bankRepository;
    private final RabbitTemplate rabbitTemplate;

    // @RabbitListener(queues =  "${rabbitmq.queue.name}", containerFactory = "rabbitListenerContainerFactory")
    public BankDTOResponse createBank(BankDTO bank) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String bankNum = uuid.substring(0, 8);
        BankModel bankModel = BankModel.builder()
                    .capital(bank.capital())
                    .bankNumber(bankNum)
                    .ownerEmail(bank.ownerEmail())
                    .name(bank.name())
                    .type(bank.type())
                    .logo(bank.logo())
                    .contact(bank.contact())
                    .build();
        bankRepository.save(bankModel);
        return new BankDTOResponse(bankModel.getName(), bankModel.getLogo(), bankModel.getOwnerEmail(), bankModel.getType(), bankModel.getCapital(), bankModel.getContact(), bankModel.getBankNumber(), bankModel.getDateCreation());
    }

    public BankWithAgenciesDTO getBank(String id){
        BankModel bank = bankRepository.findByBankNumber(id).orElse(null);
        @SuppressWarnings("unchecked")
        Set<AgencyDTO> agencies = (Set<AgencyDTO>) rabbitTemplate.convertSendAndReceive(RabbitMQConstants.AGENCY_EXCHANGE, RabbitMQConstants.AGENCY_FIND_ALL_KEY, bank);
        return new BankWithAgenciesDTO(bank, agencies);
    }

    public BankWithAgenciesDTO addAgency(AgencyDTO agency) {
        rabbitTemplate.convertSendAndReceive(RabbitMQConstants.AGENCY_EXCHANGE, RabbitMQConstants.AGENCY_CREATION_KEY, agency);
        return getBank(agency.bankId());
    }

    public AgencyDTO updateAgency(AgencyDTO agency) {
        return (AgencyDTO) rabbitTemplate.convertSendAndReceive(RabbitMQConstants.AGENCY_EXCHANGE, RabbitMQConstants.AGENCY_UPDATE_KEY, agency);
    }

    public Boolean removeAgency(AgencyDTO agency) {
        try {
            rabbitTemplate.convertAndSend(RabbitMQConstants.AGENCY_EXCHANGE, RabbitMQConstants.AGENCY_DELETE_KEY, agency);
            return true;
        }
        catch(Exception e){
            return false;
        }
    }




}
