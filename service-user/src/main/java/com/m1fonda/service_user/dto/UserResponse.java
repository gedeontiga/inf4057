package com.m1fonda.service_user.dto;

import com.m1fonda.service_user.entities.Users;

import lombok.Builder;

@Builder
public record UserResponse(
        String firstName,
        String lastName,
        String email,
        Long phoneNumber,
        String role) {
    public static UserResponse fromUser(Users user) {
        return new UserResponse(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole().getType().name());
    }

}
