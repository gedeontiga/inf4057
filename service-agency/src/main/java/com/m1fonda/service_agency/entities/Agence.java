package com.m1fonda.service_agency.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.m1fonda.entities.Agency;
import com.m1fonda.service_bank.model.BankModel;
import com.m1fonda.service_user.entities.Users;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Document(collection = "agences")
@AllArgsConstructor
@Builder
public class Agence extends Agency {

    @Id
    private Long code;

    @ManyToOne
    @JoinColumn(name = "bank_id")
    private BankModel banque;

    @OneToOne
    private Users gestionnaire;
}
