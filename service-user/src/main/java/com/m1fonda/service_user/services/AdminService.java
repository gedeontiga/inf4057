package com.m1fonda.service_user.services;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.m1fonda.commons_libs.config.RabbitMQConstants;
import com.m1fonda.commons_libs.dto.ManagerCreationAuthRequest;
import com.m1fonda.commons_libs.dto.UserRequest;
import com.m1fonda.service_user.dto.UserResponse;
import com.m1fonda.service_user.entities.Role;
import com.m1fonda.service_user.entities.RoleType;
import com.m1fonda.service_user.entities.Users;
import com.m1fonda.service_user.repositories.RoleRepository;
import com.m1fonda.service_user.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RabbitTemplate rabbitTemplate;

    public UserResponse createManager(UserRequest request) {
        Role role = roleRepository.findByType(RoleType.MANAGER).orElseThrow();
        Users user = Users.builder()
                .cni(request.cni())
                .email(request.email())
                .lastName(request.lastName())
                .firstName(request.firstName())
                .phoneNumber(request.phoneNumber())
                .role(role)
                .build();
        rabbitTemplate.convertAndSend(RabbitMQConstants.AUTH_EXCHANGE, RabbitMQConstants.AUTH_MANAGER_CREATION_KEY,
                new ManagerCreationAuthRequest(request.email(), request.password(), RoleType.MANAGER.name()));
        return UserResponse.fromUser(userRepository.save(user));
    }

    public void deleteManager(String email) {
        Role role = roleRepository.findByType(RoleType.MANAGER).orElseThrow();
        Users user = userRepository.findByEmailAndRole(email, role).orElseThrow();
        rabbitTemplate.convertAndSend(RabbitMQConstants.AUTH_EXCHANGE, RabbitMQConstants.AUTH_MANAGER_DELETION_KEY,
                user.getEmail());
        userRepository.delete(user);
    }
}
