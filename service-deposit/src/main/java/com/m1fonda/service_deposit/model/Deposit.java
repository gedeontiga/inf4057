package com.m1fonda.service_deposit.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


@Document( collection = "deposits")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Deposit {

    @Id
    private String id;

    @Indexed(unique = true)
    private String transactionNum;

    private double amount;

    private String accountNum;

    private String agencyNum;

    @Builder
    public Deposit(String transactionNum, String accountNum, double amount) {
        this.transactionNum = transactionNum;
        this.accountNum = accountNum;
        this.amount = amount;
    }

    @CreatedDate
    private final Date createdAt = new Date();
}
