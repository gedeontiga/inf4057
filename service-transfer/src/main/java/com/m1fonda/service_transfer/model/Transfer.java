package com.m1fonda.service_transfer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;


import jakarta.persistence.*;

@Document( collection = "transfers")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column( nullable = false, unique = true)
    private String transactionNum;

    @Column( nullable = false)
    private double amount;

    @ManyToOne
    @JoinColumn(nullable = false)
    private String senderAccountNum;

    @ManyToOne
    @JoinColumn(nullable = false)
    private String receiverAccountNum;

    @CreatedDate
    private Date createdAt;
}
