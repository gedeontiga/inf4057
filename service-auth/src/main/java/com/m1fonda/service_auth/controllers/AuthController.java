package com.m1fonda.service_auth.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.m1fonda.commons_libs.dto.RegistrationRequest;
import com.m1fonda.service_auth.dto.ActivationRequest;
import com.m1fonda.service_auth.dto.LoginRequest;
import com.m1fonda.service_auth.services.LoginService;
import com.m1fonda.service_auth.services.RegisterService;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

import org.springframework.http.ResponseEntity;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final RegisterService userSecurityService;
    private LoginService loginService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegistrationRequest request) {
        userSecurityService.register(request);
        return ResponseEntity.ok("Registration successful. Check your email for activation code.");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(loginService.login(request));
    }

    @PostMapping("/activate")
    public ResponseEntity<String> activate(@RequestBody ActivationRequest request) {
        userSecurityService.activate(request.email(), request.code());
        return ResponseEntity.ok("Account activated successfully");
    }
}
