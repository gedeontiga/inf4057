package com.m1fonda.commons_libs.entities;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Agency implements Serializable {

    private String numAgency;
    private String name;
    private double capital;
    private double depositBankRate;
    private double withdrawalBankRate;
    private String address;

    protected Agency(String numAgency, String name, double capital, double depositBankRate,
            double withdrawalBankRate,
            String address) {
        this.numAgency = numAgency;
        this.name = name;
        this.capital = capital;
        this.depositBankRate = depositBankRate;
        this.withdrawalBankRate = withdrawalBankRate;
        this.address = address;
    }

    protected Agency() {
    }
}
