package com.m1fonda.service_agency.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.m1fonda.entities.Demand;

import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Document(collection = "demandes")
public class Demande extends Demand {

    @Id
    private Long id;

    @Builder
    public Demande(String firstName, String lastName, String email, String password, String status, String cni,
            Long phoneNumber, Double balance, String urlRectoCni, String urlVersoCni, String numAgency) {
        super(firstName, lastName, email, password, status, cni, phoneNumber, balance, urlRectoCni, urlVersoCni,
                numAgency);
    }
}
