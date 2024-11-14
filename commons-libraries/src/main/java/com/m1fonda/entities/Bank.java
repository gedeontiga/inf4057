package com.m1fonda.entities;

import java.io.Serializable;

import lombok.Data;

@Data
public class Bank implements Serializable {
    private String nom;

    protected Bank() {
    }

    protected Bank(String nom) {
        this.nom = nom;
    }
}
