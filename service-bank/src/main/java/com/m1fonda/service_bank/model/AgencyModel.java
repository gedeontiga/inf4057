package com.m1fonda.service_bank.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.m1fonda.commons_libs.entities.Agency;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@Document(collection = "agencies")
@AllArgsConstructor
public class AgencyModel extends Agency {

    @Id
    private String id;

    private String numBank;

    @Indexed(unique = true)
    @Field("num_Agency")
    private String numAgency;

    @Builder
    public AgencyModel(String numAgency, String name, double capital, double depositBankRate, double withdrawalBankRate,
            String address, String numBank) {
        super(numAgency, name, capital, depositBankRate, withdrawalBankRate, address);
        this.numBank = numBank;
        this.numAgency = numAgency;
    }
    
}
