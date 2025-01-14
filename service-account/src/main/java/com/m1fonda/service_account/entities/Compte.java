package com.m1fonda.service_account.entities;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

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
    private Status status;
    private Date createAt = new Date();
    private String numBank;

    @DocumentReference(collection = "users")
    private Users user;

    @Builder
    public Compte(String numAccount, Double balance, Status status, Date createAt, String numAgency,
            String numBank, Users user) {
        super(numAccount, balance, numAgency);
        this.createAt = createAt;
        this.status = status;
        this.numBank = numBank;
        this.user = user;
    }

    public void setStatus(String status) {
        this.status = Status.valueOf(status);
    }
}
