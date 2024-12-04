package com.m1fonda.commons_libs.entities;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Account implements Serializable {

    private String numAccount;
    private Double balance;
    private Status status;
    private Date createAt = new Date();
    private String numAgency;
    protected String userEmail;
    private String numBank;

    protected Account(String numAccount, Double balance, Status status, Date createAt, String numAgency,
            String userEmail, String numBank) {
        this.numAccount = numAccount;
        this.balance = balance;
        this.status = status;
        this.createAt = createAt;
        this.numAgency = numAgency;
        this.userEmail = userEmail;
        this.numBank = numBank;
    }

    protected Account() {
    }

    public void setStatus(String status) {
        this.status = Status.valueOf(status);
    }
}
