package com.m1fonda.service_account.entities;

import java.util.Date;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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
    private String id;

    @Indexed(unique = true)
    @Field("comptes_user_email")
    private String userEmail;

    @Builder
    public Compte(String numAccount, Double balance, Status status, Date createAt, String numAgency, String userEmail) {
        super(numAccount, balance, status, createAt, numAgency, userEmail);
        this.userEmail = userEmail;
    }
}
