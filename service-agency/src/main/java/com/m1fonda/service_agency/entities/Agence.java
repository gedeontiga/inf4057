package com.m1fonda.service_agency.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.m1fonda.commons_libs.entities.Agency;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "agences")
public class Agence extends Agency {

    @Id
    private String id;

    @Indexed(unique = true)
    private String numAgency;

    @Override
    @Transient
    public String getNumAgency() {
        return super.getNumAgency();
    }

    @Override
    @Transient
    public void setNumAgency(String numAgency) {
        super.setNumAgency(numAgency);
    }

    @Builder
    public Agence(String numAgency, String name, double capital,
            String address, String numBank) {
        super(numAgency, name, capital, address, numBank);
        this.numAgency = numAgency;
    }
}
