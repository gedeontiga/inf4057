package com.m1fonda.service_agency.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.m1fonda.entities.Agency;

import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Document(collection = "agences")
public class Agence extends Agency {

    @Id
    private Long id;

    @Builder
    public Agence(String nom, double capital, double tauxDepotOperateur, double tauxDepotAutreOperateur,
            double tauxRetrait, String urlbank) {

        super(nom, capital, tauxDepotOperateur, tauxDepotAutreOperateur, tauxRetrait, urlbank);
    }
}
