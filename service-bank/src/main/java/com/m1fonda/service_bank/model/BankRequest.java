package com.m1fonda.service_bank.model;

import com.m1fonda.entities.Demand;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.NoArgsConstructor;
import java.util.Objects;


@Entity
@NoArgsConstructor
public class BankRequest extends Demand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String action;

    @Builder
    public BankRequest(String nom, String prenom, String email, String password, String status, String cni, Long tel,
            Double solde, String action) {
        super(nom, prenom, email, password, status, cni, tel, solde);
        this.action = action;
    }

    public String getAction() {
        return this.action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    
    

}

