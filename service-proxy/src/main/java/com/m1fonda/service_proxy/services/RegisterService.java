package com.m1fonda.service_proxy.services;

import java.security.SecureRandom;
import java.time.Instant;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.m1fonda.commons_libs.config.RabbitMQConstants;
import com.m1fonda.commons_libs.dto.ActivationRequest;
import com.m1fonda.commons_libs.dto.UserRequest;
import com.m1fonda.service_proxy.entities.Validation;
import com.m1fonda.service_proxy.entities.Role;
import com.m1fonda.service_proxy.entities.RoleType;
import com.m1fonda.service_proxy.entities.Users;
import com.m1fonda.service_proxy.repositories.ValidationRepository;
import com.m1fonda.service_proxy.repositories.RoleRepository;
import com.m1fonda.service_proxy.repositories.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RegisterService {

    private final ValidationRepository validationRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RabbitTemplate rabbitTemplate;
    private final PasswordEncoder passwordEncoder;

    private static final int ACTIVATION_CODE_LENGTH = 999999;
    private static final long ACTIVATION_HOURS_VALIDITY = 24 * 3600 * 1000;

    public boolean isEmailAlreadyExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public void register(UserRequest request) {
        // Validation
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        Role role = roleRepository.findByType(RoleType.USER).orElseThrow();

        // Création de l'utilisateur
        Users user = Users.builder()
                .password(passwordEncoder.encode(request.password()))
                .email(request.email())
                .enabled(false)
                .role(role)
                .build();
        userRepository.save(user);

        // Génération et envoi du code d'activation
        Long code = generateActivationCode();

        rabbitTemplate.convertAndSend(RabbitMQConstants.NOTIFICATION_EXCHANGE,
                RabbitMQConstants.EMAIL_NOTIFICATION_ACTIVATION_KEY,
                ActivationRequest.builder()
                        .email(request.email())
                        .activationCode(code)
                        .build());
        saveActivationCode(request.email(), code);
        rabbitTemplate.convertAndSend(RabbitMQConstants.USER_EXCHANGE, RabbitMQConstants.USER_CREATION_KEY,
                request);
    }

    public String activate(String email, Long code) {
        // Vérification du code
        Validation activationCode = validationRepository
                .findByEmailAndExpiredAfter(email, Instant.now())
                .orElseThrow(() -> new RuntimeException("Invalid or expired code"));

        // Activation de l'utilisateur
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isEnabled()) {
            return "User already activated";
        }

        if (activationCode.getActivationCode().equals(code)) {
            user.setEnabled(true);
            user = userRepository.save(user);
            return "User activated successfully";
        }
        return "Activation Failed";
    }

    private Long generateActivationCode() {
        SecureRandom random = new SecureRandom();
        return random.nextLong(ACTIVATION_CODE_LENGTH);
    }

    private void saveActivationCode(String email, Long code) {
        // Suppression de l'ancien code si existant
        validationRepository.findByEmail(email)
                .ifPresent(validationRepository::delete);

        // Sauvegarde du nouveau code
        Validation activationCode = new Validation();
        activationCode.setEmail(email);
        activationCode.setActivationCode(code);
        activationCode.setExpired(Instant.now().plusMillis(ACTIVATION_HOURS_VALIDITY));
        validationRepository.save(activationCode);
    }

    @Scheduled(cron = "@daily") // Nettoyage toutes les jours
    public void cleanupExpiredCodes() {
        validationRepository.deleteByExpiredBefore(Instant.now());
    }
}
