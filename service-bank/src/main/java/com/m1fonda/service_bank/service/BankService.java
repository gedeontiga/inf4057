package com.m1fonda.service_bank.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.m1fonda.commons_libs.config.*;
import com.m1fonda.service_bank.dto.AgencyDTO;
import com.m1fonda.service_bank.dto.BankDTO;
import com.m1fonda.service_bank.dto.BankDTOResponse;
import com.m1fonda.service_bank.dto.FeesDTO;
import com.m1fonda.service_bank.model.AgencyModel;
import com.m1fonda.service_bank.model.BankModel;
import com.m1fonda.service_bank.repository.AgencyRepository;
import com.m1fonda.service_bank.repository.BankRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class BankService {

    private final BankRepository bankRepository;
    private final RabbitTemplate rabbitTemplate;
    private final AgencyRepository agencyRepository;

    public BankDTOResponse createBank(BankDTO bank) {
        BankModel bankModel = BankModel.builder()
                    .capital(bank.capital())
                    .bankNumber(getId())
                    .ownerEmail(bank.ownerEmail())
                    .name(bank.name())
                    .type(bank.type())
                    .logo(bank.logo())
                    .withdrawFee(bank.withdrawFee())
                    .transferFee(bank.transferFee())
                    .externalFee(bank.externalFee())
                    .contact(bank.contact())
                    .build();
        BankModel b = bankRepository.save(bankModel);
        return BankDTOResponse.fromBank(b);
    }

    public BankDTOResponse getBank(String id){
        BankModel b = bankRepository.findByBankNumber(id).orElse(null);
        return BankDTOResponse.fromBank(b);
    }

    public BankDTOResponse addAgency(AgencyDTO agency) {
        AgencyModel agencyModel = AgencyModel.builder()
                                            .address(agency.address())
                                            .capital(agency.capital())
                                            .name(agency.name())
                                            .numAgency(getId())
                                            .numBank(agency.numBank())
                                            .build();
        agencyRepository.save(agencyModel);
        BankModel bankModel = bankRepository.findByBankNumber(agency.numBank()).orElse(null);
        bankModel.addAgency(agencyModel);
        bankRepository.save(bankModel);

        rabbitTemplate.convertAndSend(RabbitMQConstants.AGENCY_EXCHANGE, RabbitMQConstants.AGENCY_CREATION_KEY, agency);

        return BankDTOResponse.fromBank(bankModel);

    }

    public AgencyDTO updateAgency(AgencyDTO agency) {
        AgencyModel ag = agencyRepository.findByNumAgency(agency.numAgency()).orElse(null);
        if (agency.address() != null) ag.setAddress(agency.address());
        if (agency.capital() != 0) ag.setCapital(agency.capital());
        if (agency.name() != null) ag.setName(agency.name());
        agencyRepository.save(ag);
        rabbitTemplate.convertAndSend(RabbitMQConstants.AGENCY_EXCHANGE, RabbitMQConstants.AGENCY_UPDATE_KEY, agency);
        
        return AgencyDTO.fromAgency(ag) ;
    }

    public void removeAgency(String agencyNum) {
        agencyRepository.deleteByNumAgency(agencyNum);
        rabbitTemplate.convertAndSend(RabbitMQConstants.AGENCY_EXCHANGE, RabbitMQConstants.AGENCY_DELETE_KEY, agencyNum);
    }

    public String getId(){
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid.substring(0, 8);
    }

    public FeesDTO getFees(String agencyId){
        AgencyModel agency = agencyRepository.findByNumAgency(agencyId).orElse(null);
        BankModel bank = bankRepository.findByBankNumber(agency.getNumBank()).orElse(null);
        return FeesDTO.fromBank(bank);
    }

    public void deleteAll(){
        bankRepository.deleteAll();
        agencyRepository.deleteAll();
    }

    public List<BankDTOResponse> getAll(){
        List<BankModel> banks = bankRepository.findAll();
        List<BankDTOResponse> lists = new ArrayList<>();
        for (BankModel b: banks){
            lists.add(BankDTOResponse.fromBank(b));
        }
        return lists;
    }



}
