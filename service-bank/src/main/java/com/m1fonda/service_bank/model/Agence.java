package com.m1fonda.service_bank.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.m1fonda.commons_libs.entities.Agency;

import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Document(collection = "agences")
public class Agence extends Agency {

    @Id
    private String id;

    @Builder
    public Agence(String numAgency, String name, double capital, String address, String numBank) {
        super(numAgency, name, capital, address, numBank);
    }

}
