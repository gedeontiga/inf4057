package com.m1fonda.commons_libs.entities;

import java.io.Serializable;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public class Client implements Serializable {

    protected String numCni;
    private String firstName;
    private String lastName;
    protected String email;
    private Long phoneNumber;

    protected Client(String firstName, String lastName, Long phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }

    protected Client() {
    }
}
