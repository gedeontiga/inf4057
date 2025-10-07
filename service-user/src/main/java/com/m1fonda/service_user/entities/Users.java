package com.m1fonda.service_user.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
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
    @Field(name = "NumCni")
    @Indexed(unique = true)
    private String numCni;
    private String profilePicture;
    @DocumentReference
    private Role role;

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
    public Users(String cni, String firstName, String lastName, String email, Long phoneNumber, String profilePicture,
            String numCni, Role role, boolean enabled) {
        super(firstName, lastName, phoneNumber);
        this.profilePicture = profilePicture;
        this.numCni = numCni;
        this.email = email;
        this.role = role;
    }
}
