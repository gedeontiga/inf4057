package com.m1fonda.service_agency.entities;

import org.springframework.data.mongodb.core.mapping.Document;

import com.m1fonda.entities.Demand;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Document(collection = "demandes")
public class Demande extends Demand {

    @Id
    private Long id;

    @Builder
    public Demande(String nom, String prenom, String email, String password, String status, String cni, Long tel,
            Double solde) {
        super(nom, prenom, email, password, status, cni, tel, solde);
    }
}
