package com.m1fonda.commons_libs.entities;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Account implements Serializable {

    private String numAccount;
    private Double balance;
    protected String numAgency;

    protected Account(String numAccount, Double balance, String numAgency) {
        this.numAccount = numAccount;
        this.balance = balance;
        this.numAgency = numAgency;
    }

    protected Account() {
    }
}
