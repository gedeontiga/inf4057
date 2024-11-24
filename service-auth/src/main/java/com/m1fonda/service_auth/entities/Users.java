package com.m1fonda.service_auth.entities;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.m1fonda.commons_libs.entities.Client;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Users extends Client implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String email;
    @ManyToOne
    private Role role;
    private boolean enabled;

    @Builder
    public Users(String cni, String firstName, String lastName, String email, String password, Long phoneNumber,
            Role role, boolean enabled) {
        super(cni, firstName, lastName, email, password, phoneNumber);
        this.email = email;
        this.role = role;
        this.enabled = enabled;
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
