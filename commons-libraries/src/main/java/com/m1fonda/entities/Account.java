package com.m1fonda.entities;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Account implements Serializable {

    private String accountNumber;
    private Double balance;
    private Status status;
    private Date createAt = new Date();

    protected Account(String accountNumber, Double balance, Status status, Date createAt) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.status = status;
        this.createAt = createAt;
    }

    protected Account() {
    }
}
