package com.m1fonda.entities;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class Account implements Serializable {

    private String numero;
    private Double solde;
    private Status status;
    private Date createAt = new Date();

    protected Account(String numero, Double solde) {
        this.numero = numero;
        this.solde = solde;
    }

    protected Account() {
    }
}
