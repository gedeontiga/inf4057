package com.m1fonda.service_notif.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.m1fonda.entities.Client;

import lombok.Builder;
import lombok.NoArgsConstructor;

@Document(collection = "users")
@NoArgsConstructor
public class Users extends Client {

    @Id
    private Long id;
    private String email;

    @Builder
    public Users(String cni, String nom, String prenom, String password, Long tel, String email) {
        super(cni, nom, prenom, password, tel);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
