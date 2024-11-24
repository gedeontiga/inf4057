package com.m1fonda.service_auth.services;

import org.springframework.stereotype.Service;

import com.m1fonda.service_auth.entities.Users;
import com.m1fonda.service_auth.repositories.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Users getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
