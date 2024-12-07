package com.m1fonda.service_auth.services;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.m1fonda.commons_libs.config.RabbitMQConstants;
import com.m1fonda.commons_libs.dto.ManagerCreationAuthRequest;
import com.m1fonda.service_auth.customexceptions.UserNotFoundException;
import com.m1fonda.service_auth.entities.Role;
import com.m1fonda.service_auth.entities.RoleType;
import com.m1fonda.service_auth.entities.Users;
import com.m1fonda.service_auth.repositories.RoleRepository;
import com.m1fonda.service_auth.repositories.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public Users getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found."));
    }

    @RabbitListener(queues = RabbitMQConstants.AUTH_MANAGER_CREATION_QUEUE)
    public void createManager(ManagerCreationAuthRequest request) {
        // Validation
        Users user = userRepository.findByEmail(request.email()).orElse(Users.builder()
                .password(passwordEncoder.encode(request.password()))
                .email(request.email())
                .enabled(true)
                .build());

        Role role = roleRepository.findByType(RoleType.valueOf(request.role())).orElseThrow();
        user.setRole(role);

        userRepository.save(user);
    }

    @RabbitListener(queues = RabbitMQConstants.AUTH_MANAGER_DELETION_QUEUE)
    public void deleteManager(String email) {
        userRepository.deleteByEmail(email);
    }
}
