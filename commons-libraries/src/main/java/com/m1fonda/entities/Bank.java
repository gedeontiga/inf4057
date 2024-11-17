package com.m1fonda.entities;

import java.io.Serializable;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToOne;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@MappedSuperclass
public class Bank implements Serializable {
    private String nom;
    @OneToOne
    private Client gestionnaire;
    private String type;
    private Double capital;
    private String contact;

    protected Bank() {
    }

    protected Bank(String nom, Client gestionnaire, String type, Double capital, String contact) {
        this.nom = nom;
        this.gestionnaire = gestionnaire;
        this.type = type;
        this.capital = capital;
        this.contact = contact;
    }
}

