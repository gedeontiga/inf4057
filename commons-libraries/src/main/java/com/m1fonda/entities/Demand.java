package com.m1fonda.entities;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Demand implements Serializable {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String status; // PENDING, APPROVED, REJECTED
    private String cni;
    private Long phoneNumber;
    private Double balance;
    private String urlRectoCni;
    private String urlVersoCni;
    private String numAgency;

    protected Demand(String firstName, String lastName, String email, String password, String status, String cni,
            Long phoneNumber, Double balance, String urlRectoCni, String urlVersoCni, String numAgency) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.status = status;
        this.cni = cni;
        this.phoneNumber = phoneNumber;
        this.balance = balance;
        this.urlRectoCni = urlRectoCni;
        this.urlVersoCni = urlVersoCni;
        this.numAgency = numAgency;
    }

    protected Demand() {
    }
}
