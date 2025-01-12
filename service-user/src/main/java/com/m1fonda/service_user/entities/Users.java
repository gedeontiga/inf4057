package com.m1fonda.service_user.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

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
    @Indexed(unique = true)
    private String email;
    @Indexed(unique = true)
    private String numCni;
    @DocumentReference
    private Role role;

    @Override
    @Transient
    public String getEmail() {
        return super.getEmail();
    }

    @Override
    @Transient
    public void setEmail(String email) {
        super.setEmail(email);
    }

    @Override
    @Transient
    public String getCni() {
        return super.getCni();
    }

    @Override
    @Transient
    public void setCni(String numCni) {
        super.setCni(numCni);
    }

    @Builder
    public Users(String cni, String firstName, String lastName, String email, Long phoneNumber, String profilePicture,
            String numCni, Role role, boolean enabled) {
        super(cni, firstName, lastName, email, phoneNumber, profilePicture);
        this.numCni = numCni;
        this.email = email;
        this.role = role;
    }
}
