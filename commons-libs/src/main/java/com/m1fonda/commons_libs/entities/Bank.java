package com.m1fonda.commons_libs.entities;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Bank implements Serializable {
    private String bankNumber;
    private String name;
    private String logo;
    private String type;
    private Double capital;
    private String contact;

    protected Bank(String bankNumber, String name, String logo, String type, Double capital, String contact) {
        this.bankNumber = bankNumber;
        this.name = name;
        this.logo = logo;
        this.type = type;
        this.capital = capital;
        this.contact = contact;
    }

    protected Bank() {
    }
}
