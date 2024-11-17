package com.m1fonda.service_transfer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import com.m1fonda.entities.Account;

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
    private Long id;

    @Column( nullable = false)
    private double amount;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Account senderAccount;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Account receiverAccount;

    @CreatedDate
    private Date createdAt;
}
