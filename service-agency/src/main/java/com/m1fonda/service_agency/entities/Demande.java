package com.m1fonda.service_agency.entities;

import com.m1fonda.entities.Demand;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class Demande extends Demand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder
    public Demande(String nom, String prenom, String email, String password, String status, String cni, Long tel,
            Double solde) {
        super(nom, prenom, email, password, status, cni, tel, solde);
    }
}
