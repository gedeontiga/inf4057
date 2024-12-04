package com.m1fonda.service_bank.dto;

import java.util.Set;

import com.m1fonda.service_bank.model.Banque;

import lombok.Data;

@Data
// @SuppressWarnings("unused")
public class BankWithAgenciesDTO {
    private String bankNumber;
    private String name;
    private String logo;
    private String ownerEmail;
    private String type;
    private Double capital;
    private String contact;
    private Set<AgencyDTO> agencies;

    public BankWithAgenciesDTO(Banque bank, Set<AgencyDTO> agencies) {
        this.agencies = agencies;
        this.ownerEmail = bank.getOwnerEmail();
        this.type = bank.getType();
        this.capital = bank.getCapital();
        this.contact = bank.getContact();
        this.logo = bank.getLogo();
        this.name = bank.getName();
        this.bankNumber = bank.getBankNumber();
    }

    public BankWithAgenciesDTO() {
    }

}
