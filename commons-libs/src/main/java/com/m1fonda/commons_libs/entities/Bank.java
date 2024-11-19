package com.m1fonda.commons_libs.entities;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Bank implements Serializable {
    private String name;
    private String logo;

    protected Bank(String name, String logo) {
        this.name = name;
        this.logo = logo;
    }

    protected Bank() {
    }
}
