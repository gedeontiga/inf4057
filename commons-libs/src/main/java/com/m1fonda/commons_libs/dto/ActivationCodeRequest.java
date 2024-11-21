package com.m1fonda.commons_libs.dto;

public record ActivationCodeRequest(
        String firstName,
        String email,
        Long code) {
    public static ActivationCodeRequest activationCodeFactory(String firstName,
            String email,
            String code) {
        return new ActivationCodeRequest(firstName, email, Long.parseLong(code));
    }
}
