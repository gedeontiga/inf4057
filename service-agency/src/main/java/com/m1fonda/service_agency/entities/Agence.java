package com.m1fonda.service_agency.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.m1fonda.commons_libs.entities.Agency;

import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Document(collection = "agences")
public class Agence extends Agency {

    @Id
    private Long id;

    @DBRef
    private Banque banque;

    @Builder
    public Agence(String numAgency, String name, double capital, double depositBankRate, double withdrawalBankRate,
            String address, Banque banque) {
        super(numAgency, name, capital, depositBankRate, withdrawalBankRate, address);
        this.banque = banque;
    }
}
