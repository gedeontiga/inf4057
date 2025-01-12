package com.m1fonda.service_transfer.model;

// import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.m1fonda.commons_libs.entities.Account;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "comptes")
public class Compte extends Account {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field(name = "userMail")
    private String userEmail;

    @Override
    // @Transient
    public String getUserEmail() {
        return this.userEmail;
    }

    @Override
    // @Transient
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    @Builder
    public Compte(String numAccount, Double balance, String numAgency, String userEmail) {
        super(numAccount, balance, numAgency);
        this.userEmail = userEmail;
    }
}
