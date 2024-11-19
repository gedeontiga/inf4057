package com.m1fonda.service_account.entities;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;

import com.m1fonda.commons_libs.entities.Account;
import com.m1fonda.commons_libs.entities.Status;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Document(collection = "comptes")
@Getter
@Setter
public class Compte extends Account {

    @Id
    private Long id;
    private String numAgency;

    @Builder
    public Compte(String accountNumber, Double balance, Status status, Date createAt, String numAgency) {
        super(accountNumber, balance, status, createAt);
        this.numAgency = numAgency;
    }
}
