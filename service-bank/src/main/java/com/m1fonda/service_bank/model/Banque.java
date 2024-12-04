package com.m1fonda.service_bank.model;

import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.m1fonda.commons_libs.entities.Bank;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "banks")
@NoArgsConstructor
@Getter
@Setter
public class Banque extends Bank {

    @Id
    private String id;

    @Indexed(unique = true)
    private String ownerEmail;

    @CreatedDate
    private Date dateCreation;

    @Builder
    public Banque(String bankNumber, String name, String logo, String type, double externalTransferBankFees,
            double internalTransferBankFees, double withdrawalBankFees, Double capital, String contact,
            String ownerEmail) {
        super(bankNumber, name, logo, type, externalTransferBankFees, internalTransferBankFees, withdrawalBankFees,
                capital, contact);
        this.ownerEmail = ownerEmail;
    }

}
