package com.m1fonda.service_agency.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.m1fonda.commons_libs.entities.Bank;
import com.m1fonda.commons_libs.entities.Client;

import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Document(collection = "banque")
public class Banque extends Bank {

    @Id
    private Long id;

    @Builder
    public Banque(String name, String logo, Client owner, String type, Double capital, String contact) {
        super(name, logo, owner, type, capital, contact);
    }
}
