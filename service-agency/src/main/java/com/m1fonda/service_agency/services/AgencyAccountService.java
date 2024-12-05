package com.m1fonda.service_agency.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.m1fonda.commons_libs.config.RabbitMQConstants;
import com.m1fonda.commons_libs.dto.DemandDTO;
import com.m1fonda.service_agency.dto.DemandeDTO;
import com.m1fonda.service_agency.entities.Agence;
import com.m1fonda.service_agency.entities.Demande;
import com.m1fonda.service_agency.repositories.AgencyRepository;
import com.m1fonda.service_agency.repositories.DemandeRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AgencyAccountService {
    private final RabbitTemplate rabbitTemplate;
    private final DemandeRepository demandeRepository;
    private final AgencyRepository agencyRepository;

    public void sendDemande(Demande demande) {
        demande.setStatus("PENDING");
        Demande demand = demandeRepository.findByEmailAndNumBank(demande.getEmail(), demande.getNumBank())
                .orElse(demande);
        demand.setBalance(demande.getBalance());
        demand.setCni(demande.getCni());
        demand.setFirstName(demande.getFirstName());
        demand.setLastName(demande.getLastName());
        demand.setPhoneNumber(demande.getPhoneNumber());
        demand.setUrlRectoCni(demande.getUrlRectoCni());
        demand.setUrlVersoCni(demande.getUrlVersoCni());
        demand.setNumAgency(demande.getNumAgency());
        demand.setPassword(demande.getPassword());
        agencyRepository.findByNumAgency(demande.getNumAgency());
        demandeRepository.save(demand);
    }

    public List<DemandeDTO> getDemandes() {
        List<DemandeDTO> demandes = new ArrayList<DemandeDTO>();
        demandeRepository.findByStatus("PENDING").forEach(demande -> demandes.add(DemandeDTO.demandeFactory(demande)));
        return demandes;
    }

    public void validerDemande(String demandeId) throws Exception {
        Demande demande = demandeRepository.findById(demandeId)
                .orElseThrow(() -> new EntityNotFoundException("Demande with ID " + demandeId + " not found."));
        Agence agence = agencyRepository.findByNumAgency(demande.getNumAgency()).orElseThrow();
        rabbitTemplate.convertAndSend(RabbitMQConstants.ACCOUNT_EXCHANGE,
                RabbitMQConstants.ACCOUNT_CREATION_KEY, demande);
        demande.setStatus("APPROVED");
        agence.setCapital(agence.getCapital() + demande.getBalance());
        agencyRepository.save(agence);
        demandeRepository.save(demande);
        rabbitTemplate.convertAndSend(RabbitMQConstants.NOTIFICATION_EXCHANGE,
                RabbitMQConstants.EMAIL_NOTIFICATION_DEMAND_APPROVED_KEY, DemandDTO.demandeFactory(demande));
    }

    public void rejeterDemande(String demandeId) {
        DemandeDTO demande = DemandeDTO.demandeFactory(demandeRepository.findById(demandeId).orElseThrow());
        rabbitTemplate.convertAndSend(RabbitMQConstants.NOTIFICATION_EXCHANGE,
                RabbitMQConstants.EMAIL_NOTIFICATION_DEMAND_REJECTED_KEY, demande);
        demandeRepository.deleteById(demandeId);
    }
}
