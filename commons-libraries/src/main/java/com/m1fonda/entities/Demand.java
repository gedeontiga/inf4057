package com.m1fonda.entities;

import java.io.Serializable;

import lombok.Data;

@Data
public class Demand implements Serializable {
    private String nom;
    private String prenom;
    private String email;
    private String password;
    private String status; // PENDING, APPROVED, REJECTED
    private String cni;
    private Long tel;
    private Double solde;
    private String urlRectoCni;
    private String urlVersoCni;

    protected Demand(String nom, String prenom, String email, String password, String status, double solde, String cni,
            Long tel,
            String urlRectoCni, String urlVersoCni) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
        this.status = status;
        this.cni = cni;
        this.tel = tel;
        this.solde = solde;
        this.urlRectoCni = urlRectoCni;
        this.urlVersoCni = urlVersoCni;
    }

    protected Demand() {
    }
}
