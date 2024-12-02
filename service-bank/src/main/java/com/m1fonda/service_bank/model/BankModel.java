package com.m1fonda.service_bank.model;
import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import com.m1fonda.commons_libs.entities.*;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.NoArgsConstructor;


@Document(collection = "banks")
@Entity
@NoArgsConstructor
public class BankModel extends Bank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(unique = true, nullable = false)
    private String ownerEmail;

    @CreatedDate
    private Date dateCreation;

    @Builder
    public BankModel(String bankNumber,  String name, String logo, String ownerEmail, String type, Double capital, String contact) {
        super(bankNumber, name, logo, type, capital, contact);
        this.ownerEmail = ownerEmail;
    }

    public String getOwnerEmail() {
        return this.ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public Date getDateCreation() {
        return this.dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }


}
