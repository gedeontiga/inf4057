package com.m1fonda.service_account.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.m1fonda.commons_libs.entities.Client;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "users")
public class Users extends Client {

    @Id
    private String id;
    @Field(name = "Email")
    @Indexed(unique = true)
    private String email;

    @Override
    public String getEmail() {
        return this.email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getNumCni() {
        return this.numCni;
    }

    @Override
    public void setNumCni(String numCni) {
        this.numCni = numCni;
    }

    @Builder
    public Users(String cni, String firstName, String lastName, String email, Long phoneNumber) {
        super(firstName, lastName, phoneNumber);
        this.email = email;
    }
}
