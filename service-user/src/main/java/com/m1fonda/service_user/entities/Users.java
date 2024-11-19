package com.m1fonda.service_user.entities;

import com.m1fonda.commons_libs.entities.Client;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Users extends Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String email;

    @Builder
    public Users(String cni, String firstName, String lastName, String email, String password, Long phoneNumber) {
        super(cni, firstName, lastName, email, password, phoneNumber);
        this.email = email;
    }
}
