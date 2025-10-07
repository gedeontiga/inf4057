package com.m1fonda.service_user.services;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.m1fonda.commons_libs.config.RabbitMQConstants;
import com.m1fonda.commons_libs.dto.UserCreationRequest;
import com.m1fonda.commons_libs.dto.ManagerRequestDTO;
import com.m1fonda.commons_libs.dto.NotificationDTO;
import com.m1fonda.service_user.dto.ManagerCreationRequest;
import com.m1fonda.service_user.dto.UserResponse;
import com.m1fonda.service_user.entities.Role;
import com.m1fonda.service_user.entities.RoleType;
import com.m1fonda.service_user.entities.Users;
import com.m1fonda.service_user.repositories.RoleRepository;
import com.m1fonda.service_user.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OwnerService {

        private final UserRepository userRepository;
        private final RoleRepository roleRepository;
        private final RabbitTemplate rabbitTemplate;

        public UserResponse createManager(ManagerCreationRequest request) {
                Role role = roleRepository.findByType(RoleType.MANAGER).orElseThrow();
                Users user = Users.builder()
                                .cni(request.cni())
                                .email(request.email())
                                .lastName(request.lastName())
                                .firstName(request.firstName())
                                .phoneNumber(request.phoneNumber())
                                .profilePicture(request.profilePicture())
                                .role(role)
                                .build();
                rabbitTemplate.convertAndSend(RabbitMQConstants.AUTH_EXCHANGE, RabbitMQConstants.AUTH_USER_CREATION_KEY,
                                new UserCreationRequest(request.email(), request.password(),
                                                RoleType.MANAGER.name()));
                rabbitTemplate.convertAndSend(RabbitMQConstants.AGENCY_EXCHANGE, RabbitMQConstants.MANAGER_CREATION_KEY,
                                new ManagerRequestDTO(request.email(), request.cni(), request.numAgency()));
                rabbitTemplate.convertAndSend(RabbitMQConstants.NOTIFICATION_EXCHANGE,
                                RabbitMQConstants.USER_MAIL_NOTIFICATION_KEY,
                                new NotificationDTO(request.email(),
                                                "Vous avez ete supprime comme gestionnaire"));
                return UserResponse.fromUser(userRepository.save(user));
        }

        public void deleteManager(String email) {
                Role role = roleRepository.findByType(RoleType.MANAGER).orElseThrow();
                Users user = userRepository.findByEmailAndRole(email, role).orElseThrow();
                rabbitTemplate.convertAndSend(RabbitMQConstants.AUTH_EXCHANGE, RabbitMQConstants.AUTH_USER_DELETION_KEY,
                                user.getEmail());
                rabbitTemplate.convertAndSend(RabbitMQConstants.AGENCY_EXCHANGE, RabbitMQConstants.MANAGER_DELETION_KEY,
                                user.getEmail());
                rabbitTemplate.convertAndSend(RabbitMQConstants.NOTIFICATION_EXCHANGE,
                                RabbitMQConstants.USER_MAIL_NOTIFICATION_KEY,
                                new NotificationDTO(email,
                                                "Vous avez ete supprime comme gestionnaire"));
                userRepository.delete(user);
        }
}
