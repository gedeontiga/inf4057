package com.m1fonda.commons_libs.dto;

import com.m1fonda.commons_libs.entities.Client;

import lombok.Builder;

@Builder
public record UserRequest(
                String email,
                String password,
                String cni,
                String firstName,
                String lastName,
                Long phoneNumber) {
        public static UserRequest fromUser(Client user) {
                return new UserRequest(
                                user.getEmail(),
                                user.getPassword(),
                                user.getCni(),
                                user.getFirstName(),
                                user.getLastName(),
                                user.getPhoneNumber());
        }
}
