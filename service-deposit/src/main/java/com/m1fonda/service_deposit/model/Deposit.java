package com.m1fonda.service_deposit.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.*;

@Document( collection = "deposits")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Deposit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(unique = true, nullable = false)
    private String transactionNum;

    private double amount;

    private String accountNum;

    @CreatedDate
    private final Date createdAt = new Date();
}
