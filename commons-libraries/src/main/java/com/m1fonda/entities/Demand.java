package com.m1fonda.entities;

import java.io.Serializable;

import jakarta.persistence.MappedSuperclass;
import lombok.Data;

@Data
@MappedSuperclass
public class Demand implements Serializable {
    private String nom;
    private String prenom;
    private String email;
    private String password;
    private String status; // PENDING, APPROVED, REJECTED
    private String cni;
    private Long tel;
    private Double solde;

    protected Demand(String nom, String prenom, String email, String password, String status, String cni, Long tel,
            Double solde) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
        this.status = status;
        this.cni = cni;
        this.tel = tel;
        this.solde = solde;
    }

    protected Demand() {
    }
}
