package com.m1fonda.service_user.entities;

import java.util.Collection;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.m1fonda.commons_libs.entities.Client;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "users")
public class Users extends Client implements UserDetails {

    @Id
    private String id;
    @Field("users_email")
    @Indexed(unique = true)
    private String email;
    @DocumentReference
    private Role role;

    @Builder
    public Users(String cni, String firstName, String lastName, String email, String password, Long phoneNumber,
            Role role, boolean enabled) {
        super(cni, firstName, lastName, email, password, phoneNumber);
        this.email = email;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.role.getType().getAuthorities();
    }

    @Override
    public String getUsername() {
        return this.getEmail();
    }
}
