package com.m1fonda.service_agency.services;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.m1fonda.commons_libs.config.RabbitMQConstants;
import com.m1fonda.commons_libs.dto.DemandeDTO;
import com.m1fonda.service_agency.entities.Agence;
import com.m1fonda.service_agency.entities.Demande;
import com.m1fonda.service_agency.repositories.AgencyRepository;
import com.m1fonda.service_agency.repositories.DemandeRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AgencyAccountService {

    private final RabbitTemplate rabbitTemplate;
    private final DemandeRepository demandeRepository;
    private final AgencyRepository agencyRepository;

    public DemandeDTO processDemande(Demande demande) {
        boolean isApproved = validerDemande(demande);

        Agence agence = agencyRepository.findByNumAgency(demande.getNumAgency()).orElse(null);
        if (isApproved && agence != null) {
            demande.setStatus("APPROVED");
            rabbitTemplate.convertAndSend(RabbitMQConstants.ACCOUNT_EXCHANGE, RabbitMQConstants.ACCOUNT_CREATION_KEY,
                    demande);

            agence.setCapital(agence.getCapital() + demande.getBalance());
            agencyRepository.save(agence);
            return DemandeDTO.demandeFactory(demandeRepository.save(demande));
            // System.out.println("Demande approuvée : " + demande);
        } else {
            demande.setStatus("REJECTED");
            return DemandeDTO.demandeFactory(demande);
        }
    }

    private boolean validerDemande(Demande demande) {
        return demande.getCni().length() == 11 &&
                demande.getPhoneNumber().toString().length() == 9
                && demande.getBalance() >= 2000;
    }
}
