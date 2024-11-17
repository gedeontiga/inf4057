package com.m1fonda.service_withdrawal.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import com.m1fonda.entities.Account;

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
    private Long id;

    @Column( nullable = false)
    private double amount;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Account account;

    @CreatedDate
    private Date createdAt;
}
