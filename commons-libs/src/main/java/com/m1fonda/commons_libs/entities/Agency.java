package com.m1fonda.commons_libs.entities;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Agency implements Serializable {

    protected String numAgency;
    private String name;
    private double capital;
    private String address;
    private String numBank;

    protected Agency(String numAgency, String name, double capital, String address,
            String numBank) {
        this.numAgency = numAgency;
        this.name = name;
        this.capital = capital;
        this.address = address;
        this.numBank = numBank;
    }

    protected Agency() {
    }
}
