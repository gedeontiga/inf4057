package com.m1fonda.service_proxy.dto;

import com.m1fonda.service_proxy.entities.Role;
import com.m1fonda.service_proxy.entities.Users;

public record AuthResponse(
        String email,
        boolean enabled,
        Role role) {
    public static AuthResponse from(Users user) {
        return new AuthResponse(user.getEmail(),
                user.isEnabled(),
                user.getRole());
    }
}
