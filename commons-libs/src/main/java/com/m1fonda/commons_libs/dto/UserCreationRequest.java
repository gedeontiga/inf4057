package com.m1fonda.commons_libs.dto;

public record UserCreationRequest(
        String email,
        String password,
        String role) {
}
