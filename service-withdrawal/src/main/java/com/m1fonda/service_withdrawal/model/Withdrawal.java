package com.m1fonda.service_withdrawal.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;


import jakarta.persistence.*;

@Document( collection = "withdrawals")

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Withdrawal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column( nullable = false, unique = true)
    private String transactionNum;

    @Column( nullable = false)
    private double amount;

    @ManyToOne
    @JoinColumn(nullable = false)
    private String accountNum;

    @CreatedDate
    private Date createdAt;
}
