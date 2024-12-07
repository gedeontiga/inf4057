package com.m1fonda.commons_libs.entities;

import java.io.Serializable;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public class Client implements Serializable {

    private String cni;
    private String firstName;
    private String lastName;
    protected String email;
    private Long phoneNumber;

    protected Client(String cni, String firstName, String lastName, String email, Long phoneNumber) {
        this.cni = cni;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    protected Client() {
    }
}
