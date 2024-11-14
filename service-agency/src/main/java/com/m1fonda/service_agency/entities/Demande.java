package com.m1fonda.service_agency.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.m1fonda.entities.Demand;

import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Document(collection = "demandes")
public class Demande extends Demand {

    @Id
    private Long id;

    @Builder
    public Demande(String nom, String prenom, String email, String password, String status, double solde, String cni,
            Long tel, String urlRectoCni, String urlVersoCni) {
        super(nom, prenom, email, password, status, solde, cni, tel, urlRectoCni, urlVersoCni);
    }
}
