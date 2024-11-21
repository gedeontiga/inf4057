package com.m1fonda.service_auth.services;

import java.security.SecureRandom;
import java.time.Instant;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.m1fonda.commons_libs.config.RabbitMQConstants;
import com.m1fonda.commons_libs.dto.ActivationCodeRequest;
import com.m1fonda.commons_libs.dto.UserRequest;
import com.m1fonda.service_auth.entities.Validation;
import com.m1fonda.service_auth.entities.Role;
import com.m1fonda.service_auth.entities.RoleType;
import com.m1fonda.service_auth.entities.Users;
import com.m1fonda.service_auth.repositories.ValidationRepository;
import com.m1fonda.service_auth.repositories.RoleRepository;
import com.m1fonda.service_auth.repositories.UserRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RegisterService {

    private static final String REGISTER_FALLBACK = "registerFallback";
    private static final String SERVICE_USER_CIRCUIT_BREAKER = "serviceUserCircuitBreaker";
    private final ValidationRepository validationRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RabbitTemplate rabbitTemplate;
    private final PasswordEncoder passwordEncoder;

    private static final int ACTIVATION_CODE_LENGTH = 6;
    private static final long ACTIVATION_HOURS_VALIDITY = 24 * 3600 * 1000;

    @CircuitBreaker(name = SERVICE_USER_CIRCUIT_BREAKER, fallbackMethod = REGISTER_FALLBACK)
    @RabbitListener(queues = RabbitMQConstants.AUTH_REGISTER_QUEUE)
    public void register(UserRequest request) {
        // Validation
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        Role role = roleRepository.findByType(RoleType.USER);

        // Création de l'utilisateur
        Users user = Users.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .cni(request.cni())
                .password(passwordEncoder.encode(request.password()))
                .phoneNumber(request.phoneNumber())
                .email(request.email())
                .enabled(false)
                .role(role)
                .build();
        userRepository.save(user);

        // Génération et envoi du code d'activation
        String code = generateActivationCode();
        saveActivationCode(request.email(), code);
        sendActivationEmail(request.firstName(), request.email(), code);
    }

    @Transactional
    public void activate(String email, String code) {
        // Vérification du code
        Validation activationCode = validationRepository
                .findByEmailAndExpiredAfter(email, Instant.now())
                .orElseThrow(() -> new RuntimeException("Invalid or expired code"));

        // Activation de l'utilisateur
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isEnabled()) {
            throw new RuntimeException("User already activated");
        }

        if (activationCode.getCode().equals(code)) {
            user.setEnabled(true);
            user = userRepository.save(user);
            rabbitTemplate.convertAndSend(RabbitMQConstants.USER_EXCHANGE, RabbitMQConstants.USER_CREATION_KEY,
                    UserRequest.fromUser(user));
        }
    }

    private String generateActivationCode() {
        SecureRandom random = new SecureRandom();
        return String.format("%0" + ACTIVATION_CODE_LENGTH + "d",
                random.nextInt((int) Math.pow(10, ACTIVATION_CODE_LENGTH)));
    }

    private void saveActivationCode(String email, String code) {
        // Suppression de l'ancien code si existant
        validationRepository.findByEmail(email)
                .ifPresent(validationRepository::delete);

        // Sauvegarde du nouveau code
        Validation activationCode = new Validation();
        activationCode.setEmail(email);
        activationCode.setCode(code);
        activationCode.setExpired(Instant.now().plusMillis(ACTIVATION_HOURS_VALIDITY));
        validationRepository.save(activationCode);
    }

    private void sendActivationEmail(String firstName, String email, String code) {
        rabbitTemplate.convertAndSend(RabbitMQConstants.NOTIFICATION_EXCHANGE,
                RabbitMQConstants.EMAIL_NOTIFICATION_ACTIVATION_KEY,
                ActivationCodeRequest.activationCodeFactory(firstName, email, code));
    }

    @Scheduled(cron = "@daily") // Nettoyage toutes les jours
    public void cleanupExpiredCodes() {
        validationRepository.deleteByExpiredBefore(Instant.now());
    }

    public void registerFallback(UserRequest registrationRequest, Throwable throwable) {
        System.out.println("Fallback - Inscription échouée: " + throwable.getCause());
    }
}
