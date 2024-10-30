package com.m1fonda.entities;

import java.io.Serializable;

import jakarta.persistence.MappedSuperclass;
import lombok.Data;

@Data
@MappedSuperclass
public class Client implements Serializable {

    private String cni;
    private String nom;
    private String prenom;
    private String password;
    private Long tel;

    protected Client(String cni, String nom, String prenom, String password, Long tel) {
        this.cni = cni;
        this.nom = nom;
        this.prenom = prenom;
        this.password = password;
        this.tel = tel;
    }

    protected Client() {
    }
}
