package com.m1fonda.service_agency.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.m1fonda.commons_libs.entities.Agency;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "agences")
public class Agence extends Agency {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("agences_number")
    private String numAgency;

    private String numBank;

    @Builder
    public Agence(String numAgency, String name, double capital, double depositBankRate, double withdrawalBankRate,
            String address, String numBank) {
        super(numAgency, name, capital, depositBankRate, withdrawalBankRate, address);
        this.numBank = numBank;
        this.numAgency = numAgency;
    }
}
