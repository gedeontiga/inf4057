package com.m1fonda.service_user.dto;

import com.m1fonda.service_user.entities.Users;

import lombok.Builder;

@Builder
public record ManagerCreationRequest(
        String email,
        String password,
        String cni,
        String firstName,
        String lastName,
        Long phoneNumber,
        String profilePicture,
        String numAgency) {
    public static ManagerCreationRequest fromUser(Users user, String password, String numAgency) {
        return new ManagerCreationRequest(
                user.getEmail(),
                password,
                user.getCni(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                user.getProfilePicture(),
                numAgency);
    }
}
