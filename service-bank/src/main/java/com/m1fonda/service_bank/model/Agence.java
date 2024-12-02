package com.m1fonda.service_bank.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.m1fonda.commons_libs.entities.*;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Document(collection = "agences")
@AllArgsConstructor
@Builder
public class Agence extends Agency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @ManyToOne
    private BankModel bank;

    public String getId() {
        return this.id;
    }

    public BankModel getBank() {
        return this.bank;
    }

    public void setBank(BankModel bank) {
        this.bank = bank;
    }

    public Agence(String numAgency, String name, double capital, double depositBankRate, double withdrawalBankRate,
            String address, BankModel bank) {
        super(numAgency, name, capital, depositBankRate, withdrawalBankRate, address);
        this.bank = bank;
    }

    
}
