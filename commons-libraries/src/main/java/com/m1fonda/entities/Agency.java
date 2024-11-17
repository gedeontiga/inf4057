package com.m1fonda.entities;

import java.io.Serializable;

import jakarta.persistence.*;
import lombok.Data;

@Data
public class Agency implements Serializable {

    private String nom;
    private double capital;
    private double tauxDepotOperateur;
    private double tauxDepotAutreOperateur;
    private double tauxRetrait;
    @ManyToOne
    @JoinColumn(name = "bank_id")
    private Bank banque;
    private Client gestionnaire;
    private String address;

    protected Agency() {
    }

    protected Agency(String nom, double capital, double tauxDepotOperateur, double tauxDepotAutreOperateur,
            double tauxRetrait, Bank banque, Client gestionnaire, String address) {
        this.nom = nom;
        this.capital = capital;
        this.tauxDepotOperateur = tauxDepotOperateur;
        this.tauxDepotAutreOperateur = tauxDepotAutreOperateur;
        this.tauxRetrait = tauxRetrait;
        this.banque = banque;
        this.address = address;
        this.gestionnaire = gestionnaire;
    }
}
