package com.m1fonda.service_auth.services;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.m1fonda.commons_libs.config.RabbitMQConstants;
import com.m1fonda.commons_libs.dto.UserCreationRequest;
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

    @RabbitListener(bindings = @QueueBinding(value = @Queue(RabbitMQConstants.AUTH_USER_CREATION_QUEUE), exchange = @Exchange(value = RabbitMQConstants.AUTH_EXCHANGE, type = ExchangeTypes.DIRECT), key = RabbitMQConstants.AUTH_USER_CREATION_KEY))
    public void createManager(UserCreationRequest request) {
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

    @RabbitListener(bindings = @QueueBinding(value = @Queue(RabbitMQConstants.AUTH_USER_DELETION_QUEUE), exchange = @Exchange(value = RabbitMQConstants.AUTH_EXCHANGE, type = ExchangeTypes.DIRECT), key = RabbitMQConstants.AUTH_USER_DELETION_KEY))
    public void deleteManager(String email) {
        userRepository.deleteByEmail(email);
    }
}
