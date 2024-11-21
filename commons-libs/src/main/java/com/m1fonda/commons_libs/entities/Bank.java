package com.m1fonda.commons_libs.entities;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Bank implements Serializable {
    private String name;
    private String logo;
    private Client owner;
    private String type;
    private Double capital;
    private String contact;

    protected Bank(String name, String logo, Client owner, String type, Double capital, String contact) {
        this.name = name;
        this.logo = logo;
        this.owner = owner;
        this.type = type;
        this.capital = capital;
        this.contact = contact;
    }

    protected Bank() {
    }
}
