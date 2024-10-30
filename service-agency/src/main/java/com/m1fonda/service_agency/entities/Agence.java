package com.m1fonda.service_agency.entities;

import com.m1fonda.entities.Agency;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class Agence extends Agency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder
    public Agence(String nom, double capital, double tauxDepotOperateur, double tauxDepotAutreOperateur,
            double tauxRetrait, String urlbank) {

        super(nom, capital, tauxDepotOperateur, tauxDepotAutreOperateur, tauxRetrait, urlbank);
    }
}
