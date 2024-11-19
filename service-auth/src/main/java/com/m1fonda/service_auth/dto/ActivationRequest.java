package com.m1fonda.service_auth.dto;

public record ActivationRequest(
        String email,
        String code) {
}
