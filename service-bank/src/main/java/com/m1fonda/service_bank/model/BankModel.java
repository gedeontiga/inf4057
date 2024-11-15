package com.m1fonda.service_bank.model;
import java.util.Date;

import org.springframework.data.annotation.CreatedDate;

import com.m1fonda.entities.Bank;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import com.m1fonda.service_user.entities.Users;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class BankModel extends Bank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Users gestionnaire;

    @CreatedDate
    private Date dateCreation;
}
