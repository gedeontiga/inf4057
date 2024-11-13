package com.m1fonda.entities;

import java.util.Date;

import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@MappedSuperclass
public class Bank {

    private String nom;
    private Client gestionnaire;
    private String type;
    private Date dateCreation;
    private Double capital;
    private String contact;

}
