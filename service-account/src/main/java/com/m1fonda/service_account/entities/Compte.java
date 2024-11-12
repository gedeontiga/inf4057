package com.m1fonda.service_account.entities;

import org.springframework.data.mongodb.core.mapping.Document;

import com.m1fonda.entities.Account;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Document(collection = "comptes")
public class Compte extends Account {

    @Id
    private Long id;

    @Builder
    public Compte(String numero, Double solde) {
        super(numero, solde);
    }
}
