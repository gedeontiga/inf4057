package com.m1fonda.entities;

import java.io.Serializable;

import lombok.Data;

@Data
public class Agency implements Serializable {

    private String nom;
    private double capital;
    private double tauxDepotOperateur;
    private double tauxDepotAutreOperateur;
    private double tauxRetrait;
    private String urlbank;

    protected Agency() {
    }

    protected Agency(String nom, double capital, double tauxDepotOperateur, double tauxDepotAutreOperateur,
            double tauxRetrait, String urlbank) {
        this.nom = nom;
        this.capital = capital;
        this.tauxDepotOperateur = tauxDepotOperateur;
        this.tauxDepotAutreOperateur = tauxDepotAutreOperateur;
        this.tauxRetrait = tauxRetrait;
        this.urlbank = urlbank;
    }
}
