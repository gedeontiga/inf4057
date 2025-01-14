package com.m1fonda.commons_libs.dto;

import com.m1fonda.commons_libs.entities.Client;

public record UserInfoUpdate(
        String email,
        String firstName,
        String lastName,
        Long phoneNumber) {
    public static UserInfoUpdate fromUser(Client user) {
        return new UserInfoUpdate(
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber());
    }
}
