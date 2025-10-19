package com.m1fonda.service_proxy.dto;

import jakarta.validation.constraints.Size;

public record PasswordResetRequest(String token, @Size(min = 8) String newPassword) {
}