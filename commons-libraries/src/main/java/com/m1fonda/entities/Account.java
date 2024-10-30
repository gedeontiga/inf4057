package com.m1fonda.entities;

import java.io.Serializable;

import jakarta.persistence.MappedSuperclass;
import lombok.Data;

@Data
@MappedSuperclass
public class Account implements Serializable {

    private String numero;
    private Double solde;
    private Status status;

    protected Account(String numero, Double solde) {
        this.numero = numero;
        this.solde = solde;
    }

    protected Account() {
    }
}
