package com.m1fonda.service_proxy.dto;

public record LoginResponse(String bearer, String role, String expiresAt, String expiresIn) {
}
