package com.m1fonda.service_proxy.services;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.m1fonda.service_proxy.entities.Users;
import com.m1fonda.service_proxy.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) {
        try {
            return userRepository.findByEmailAndEnabledIsTrue(email)
                    .orElseThrow(() -> new DisabledException("User is not activated"));
        } catch (Exception e) {
            Users user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
            if (!user.isEnabled()) {
                throw new DisabledException("User is not activated");
            }
            throw new EntityNotFoundException("User not found");
        }
    }
}
