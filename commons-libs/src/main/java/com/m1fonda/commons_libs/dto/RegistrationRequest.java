package com.m1fonda.commons_libs.dto;

import com.m1fonda.commons_libs.entities.Client;

import lombok.Builder;

@Builder
public record RegistrationRequest(
                String email,
                String password,
                String cni,
                String firstName,
                String lastName,
                Long phoneNumber) {
        public static RegistrationRequest fromUser(Client user) {
                return new RegistrationRequest(
                                user.getEmail(),
                                user.getPassword(),
                                user.getCni(),
                                user.getFirstName(),
                                user.getLastName(),
                                user.getPhoneNumber());
        }
}
