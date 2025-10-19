package com.m1fonda.service_proxy.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.m1fonda.commons_libs.dto.UserRequest;
import com.m1fonda.service_proxy.dto.LoginRequest;
import com.m1fonda.service_proxy.dto.LoginResponse;
import com.m1fonda.service_proxy.services.AuthService;

import reactor.core.publisher.Mono;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public Mono<ResponseEntity<String>> register(@Valid @RequestBody UserRequest request) {
        return Mono.fromRunnable(() -> authService.register(request))
                .then(Mono.just(ResponseEntity.status(HttpStatus.CREATED)
                        .body("User registered successfully. Please check your email for activation.")))
                .onErrorResume(e -> {
                    log.error("Registration failed: {}", e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Registration failed: " + e.getMessage()));
                });
    }

    @PatchMapping("/activate")
    public Mono<ResponseEntity<String>> activate(@RequestParam Long token) {
        return Mono.fromRunnable(() -> authService.activate(token))
                .then(Mono.just(ResponseEntity.ok("Account activated successfully")))
                .onErrorResume(e -> {
                    log.error("Activation failed: {}", e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Activation failed: " + e.getMessage()));
                });
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> {
                    log.error("Login failed: {}", e.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(null));
                });
    }

    // @PostMapping("/reset-password-request")
    // public Mono<ResponseEntity<String>> requestPasswordReset(@RequestParam String
    // email) {
    // return Mono.fromRunnable(() -> authService.requestPasswordReset(email))
    // .then(Mono.just(ResponseEntity.ok("Password reset email sent")))
    // .onErrorResume(e -> {
    // log.error("Password reset request failed: {}", e.getMessage());
    // return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
    // .body("Password reset request failed: " + e.getMessage()));
    // });
    // }

    // @PostMapping("/reset-password")
    // public Mono<ResponseEntity<String>> resetPassword(@Valid @RequestBody
    // PasswordResetRequest request) {
    // return Mono.fromRunnable(() -> authService.resetPassword(request))
    // .then(Mono.just(ResponseEntity.ok("Password reset successful")))
    // .onErrorResume(e -> {
    // log.error("Password reset failed: {}", e.getMessage());
    // return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
    // .body("Password reset failed: " + e.getMessage()));
    // });
    // }
}
