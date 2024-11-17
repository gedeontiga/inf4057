package com.m1fonda.service_bank.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.m1fonda.config.RabbitMQConstants;
import com.m1fonda.dto.AgencyCreateRequest;
import com.m1fonda.dto.AgencyUpdateRequest;
import com.m1fonda.service_bank.model.BankModel;
import com.m1fonda.service_bank.repository.BankRepository;

import lombok.AllArgsConstructor;

@Component
@Service
@AllArgsConstructor
public class BankService {

    private final BankRepository bankRepository;
    private final RabbitTemplate rabbitTemplate;

    // @RabbitListener(queues =  "${rabbitmq.queue.name}", containerFactory = "rabbitListenerContainerFactory")
    public BankModel createBank(BankModel bank) {
        return bankRepository.save(bank);
    }

    public BankModel getBank(Long id){
        return bankRepository.findById(id).orElse(null);
    }

    public Object addAgency(AgencyCreateRequest agency) {
        return rabbitTemplate.convertSendAndReceive(RabbitMQConstants.AGENCY_EXCHANGE, RabbitMQConstants.AGENCY_CREATION_KEY, agency);
    }

    public Object updateAgency(AgencyUpdateRequest agency) {
        return rabbitTemplate.convertSendAndReceive(RabbitMQConstants.AGENCY_EXCHANGE, RabbitMQConstants.AGENCY_UPDATE_KEY, agency);
    }

    public void removeAgency(Long agencyId) {
        rabbitTemplate.convertAndSend(RabbitMQConstants.AGENCY_EXCHANGE, RabbitMQConstants.AGENCY_DELETE_KEY, agencyId);
    }




}
