package com.m1fonda.service_proxy.services;

import java.time.Instant;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.m1fonda.commons_libs.config.RabbitMQConstants;
import com.m1fonda.commons_libs.dto.ActivationRequest;
import com.m1fonda.commons_libs.dto.UserRequest;
import com.m1fonda.service_proxy.dto.LoginRequest;
import com.m1fonda.service_proxy.dto.LoginResponse;
import com.m1fonda.service_proxy.entities.Role;
import com.m1fonda.service_proxy.entities.RoleType;
import com.m1fonda.service_proxy.entities.Users;
import com.m1fonda.service_proxy.entities.Validation;
import com.m1fonda.service_proxy.repositories.RoleRepository;
import com.m1fonda.service_proxy.repositories.UserRepository;
import com.m1fonda.service_proxy.repositories.ValidationRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@AllArgsConstructor
public class AuthService {
    private static final long ACTIVATION_HOURS_VALIDITY = 24 * 3600 * 1000;
    private final ReactiveAuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final ValidationRepository validationRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final RabbitTemplate rabbitTemplate;

    public Boolean isEmailAlreadyExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public void register(UserRequest request) {
        validateRequest(request.email());
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

    public void activate(Long code) {
        Validation activation = validationRepository
                .findByActivationCodeAndExpiredAfter(code, Instant.now())
                .orElseThrow(() -> new RuntimeException("Invalid or expired activation link"));

        if (!activation.getActivationCode().equals(code)) {
            throw new DisabledException("Activation Failed: invalid code");
        }

        userRepository.findByEmail(activation.getEmail()).ifPresent(updatedUser -> {
            updatedUser.setEnabled(true);
            userRepository.save(updatedUser);
        });
        validationRepository.delete(activation);
    }

    public Mono<LoginResponse> login(LoginRequest request) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(request.email(),
                request.password());

        return authenticationManager.authenticate(authToken)
                .map(authentication -> {
                    Users user = (Users) authentication.getPrincipal();
                    return jwtService.generate(request.email(), user.getRole().getType());
                })
                .onErrorMap(DisabledException.class,
                        e -> new BadCredentialsException("User not found or account is not activated"))
                .onErrorMap(BadCredentialsException.class,
                        e -> new BadCredentialsException("Invalid login credentials"))
                .onErrorMap(AuthenticationException.class,
                        e -> new BadCredentialsException("Authentication failed: " + e.getMessage()));
    }

    // public void requestPasswordReset(String email) {
    // Users user = userRepository.findByEmail(email)
    // .orElseThrow(() -> new EntityNotFoundException("User not found with email: "
    // + email));

    // if (!user.isEnabled()) {
    // throw new DisabledException("Account not activated");
    // }

    // String code = generateActivationToken();
    // saveToken(user, code);
    // }

    // public void resetPassword(PasswordResetRequest request) {
    // Validation resetValidation = validationRepository
    // .findByActivationTokenAndExpiredIsAfter(request.code(), Instant.now())
    // .orElseThrow(() -> new DisabledException("Invalid or expired reset code"));

    // Users user = resetValidation.getUser();
    // userRepository.findByEmail(user.getEmail()).ifPresent(updatedUser -> {
    // updatedUser.setPassword(passwordEncoder.encode(request.newPassword()));
    // userRepository.save(updatedUser);
    // });
    // validationRepository.delete(resetValidation);
    // }

    private Long generateActivationCode() {
        return UUID.randomUUID().getMostSignificantBits();
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

    private void validateRequest(String email) {
        if (isEmailAlreadyExists(email)) {
            throw new RuntimeException("Email already registered");
        }
    }
}
