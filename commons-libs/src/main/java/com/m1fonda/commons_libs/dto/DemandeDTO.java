package com.m1fonda.commons_libs.dto;

import com.m1fonda.commons_libs.entities.Demand;

public record DemandeDTO(
        String firstName,
        String lastName,
        String email,
        String status,
        Long phoneNumber,
        Double balance,
        String numAgency) {
    public static DemandeDTO demandeFactory(Demand demand) {
        return new DemandeDTO(demand.getFirstName(), demand.getLastName(), demand.getEmail(), demand.getStatus(),
                demand.getPhoneNumber(), demand.getBalance(), demand.getNumAgency());
    }

}
