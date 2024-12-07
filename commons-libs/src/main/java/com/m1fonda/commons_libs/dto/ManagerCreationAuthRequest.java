package com.m1fonda.commons_libs.dto;

public record ManagerCreationAuthRequest(
                String email,
                String password,
                String role) {
}
