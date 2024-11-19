package com.m1fonda.service_user.services;

import java.util.Optional;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.m1fonda.commons_libs.config.RabbitMQConstants;
import com.m1fonda.commons_libs.dto.RegistrationRequest;
import com.m1fonda.service_user.dto.UserResponse;
import com.m1fonda.service_user.entities.Users;
import com.m1fonda.service_user.repositories.UserRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {

    private static final String USER_CREATION_FALLBACK = "userCreationFallback";
    private static final String USER_CREATION_CIRCUIT_BREAKER = "userCreationCircuitBreaker";
    private final UserRepository userRepository;

    public UserResponse read(String email) {
        Users user = userRepository.findByEmail(email).orElseThrow();
        return UserResponse.fromUser(user);
    }

    public UserResponse update(RegistrationRequest request) {
        Users user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setFirstName(Optional.ofNullable(request.firstName()).orElse(user.getFirstName()));
        user.setLastName(Optional.ofNullable(request.lastName()).orElse(user.getLastName()));
        user.setPhoneNumber(Optional.ofNullable(request.phoneNumber()).orElse(user.getPhoneNumber()));
        user.setPassword(Optional.ofNullable(request.password()).orElse(user.getPassword()));
        user.setCni(Optional.ofNullable(request.cni()).orElse(user.getCni()));
        return UserResponse.fromUser(userRepository.save(user));
    }

    @CircuitBreaker(name = USER_CREATION_CIRCUIT_BREAKER, fallbackMethod = USER_CREATION_FALLBACK)
    @RabbitListener(queues = RabbitMQConstants.USER_CREATION_QUEUE)
    public UserResponse create(RegistrationRequest request) {
        Users user = Users.builder()
                .cni(request.cni())
                .email(request.email())
                .lastName(request.lastName())
                .firstName(request.firstName())
                .password(request.password())
                .phoneNumber(request.phoneNumber())
                .build();
        return UserResponse.fromUser(userRepository.save(user));
    }

    public void userCreationFallback(RegistrationRequest request, Throwable throwable) {
        System.out.println("Fallback - Demande de création d'utilisateur a échoué : " + request.toString());
        System.out.println("Cause de l'échec : " + throwable.getMessage());
    }
}
