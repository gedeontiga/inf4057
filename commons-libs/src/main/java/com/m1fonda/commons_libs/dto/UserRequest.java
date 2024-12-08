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
                Long phoneNumber,
                String profilePicture) {
        public static UserRequest fromUser(Client user, String password) {
                return new UserRequest(
                                user.getEmail(),
                                password,
                                user.getCni(),
                                user.getFirstName(),
                                user.getLastName(),
                                user.getPhoneNumber(),
                                user.getProfilePicture());
        }
}
