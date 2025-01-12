package com.m1fonda.service_account.entities;

import java.util.Date;

// import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.m1fonda.commons_libs.entities.Account;
import com.m1fonda.commons_libs.entities.Status;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Document(collection = "comptes")
@Getter
@Setter
public class Compte extends Account {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field(name = "userMail")
    private String userEmail;
    private Status status;
    private Date createAt = new Date();
    private String numBank;

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
    public Compte(String numAccount, Double balance, Status status, Date createAt, String numAgency, String userEmail,
            String numBank) {
        super(numAccount, balance, numAgency);
        this.userEmail = userEmail;
        this.createAt = createAt;
        this.status = status;
        this.numBank = numBank;
    }

    public void setStatus(String status) {
        this.status = Status.valueOf(status);
    }
}
