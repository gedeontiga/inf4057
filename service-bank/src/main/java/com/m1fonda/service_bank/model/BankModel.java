package com.m1fonda.service_bank.model;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import com.m1fonda.commons_libs.entities.Bank;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Document(collection = "banks")
@NoArgsConstructor
@Getter
@Setter
public class BankModel extends Bank {

    @Id
    private String id;

    @Indexed(unique = true)
    private String ownerEmail;

    @CreatedDate
    private Date dateCreation = new Date();

    private double withdrawFee;
    private double transferFee;

    @DocumentReference
    private Set<AgencyModel> agencies;

    @Builder
    public BankModel(String bankNumber,  String name, String logo, String ownerEmail, String type, Double capital, String contact, double withdrawFee, double transferFee) {
        super(bankNumber, name, logo, type, capital, contact);
        this.ownerEmail = ownerEmail;
        this.agencies = new HashSet<>();
        this.transferFee = transferFee;
        this.withdrawFee = withdrawFee;
    }

    public void addAgency(AgencyModel agencyModel) {
        this.agencies.add(agencyModel);
    }

    public void removeAgency(AgencyModel agencyModel) {
        this.agencies.remove(agencyModel);
    }

}
