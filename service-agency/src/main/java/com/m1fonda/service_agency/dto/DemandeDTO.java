package com.m1fonda.service_agency.dto;

import com.m1fonda.service_agency.entities.Demande;

public record DemandeDTO(
        String id,
        String firstName,
        String lastName,
        String email,
        String status,
        Long phoneNumber,
        Double balance,
        String numAgency) {
    public static DemandeDTO demandeFactory(Demande demande) {
        return new DemandeDTO(demande.getId(), demande.getFirstName(), demande.getLastName(), demande.getEmail(),
                demande.getStatus(),
                demande.getPhoneNumber(), demande.getBalance(), demande.getNumAgency());
    }
}
