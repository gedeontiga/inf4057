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

    protected Account(String numAccount, Double balance, Status status, Date createAt, String numAgency) {
        this.numAccount = numAccount;
        this.balance = balance;
        this.status = status;
        this.createAt = createAt;
        this.numAgency = numAgency;
    }

    protected Account() {
    }

    public void setStatus(String status) {
        this.status = Status.valueOf(status);
    }
}
