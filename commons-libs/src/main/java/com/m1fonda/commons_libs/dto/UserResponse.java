package com.m1fonda.commons_libs.dto;

public record UserResponse(
        String firstName,
        String lastName,
        String email,
        Long phoneNumber) {}
