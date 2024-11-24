package com.m1fonda.service_auth.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.m1fonda.commons_libs.dto.UserRequest;
import com.m1fonda.service_auth.dto.ActivationRequest;
import com.m1fonda.service_auth.dto.LoginRequest;
import com.m1fonda.service_auth.entities.Users;
import com.m1fonda.service_auth.services.JwtService;
import com.m1fonda.service_auth.services.LoginService;
import com.m1fonda.service_auth.services.RegisterService;
import com.m1fonda.service_auth.services.UserService;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final RegisterService registerService;
    private LoginService loginService;
    private final JwtService jwtService;
    private final UserService userService;

    @PostMapping("/validate")
    public ResponseEntity<Map<String, String>> validateToken(
            @RequestHeader(org.springframework.http.HttpHeaders.AUTHORIZATION) String bearerToken) {
        String token = bearerToken.substring(7);

        if (jwtService.isTokenExpired(token)) {
            throw new RuntimeException("Token expired");
        }

        String email = jwtService.getEmailFromToken(token);
        Users user = userService.getUserByEmail(email);

        Map<String, String> response = new HashMap<>();
        response.put("email", user.getEmail());
        response.put("roles", user.getRole().getType().toString());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/check/{email}")
    public ResponseEntity<Boolean> checkEmail(@PathVariable String email) {
        return ResponseEntity.ok(registerService.isEmailAlreadyExists(email));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRequest request) {
        registerService.register(request);
        return ResponseEntity.ok("Registration successful. Check your email for activation code.");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(loginService.login(request));
    }

    @PostMapping("/activate")
    public ResponseEntity<String> activate(@RequestBody ActivationRequest request) {
        registerService.activate(request.email(), request.code());
        return ResponseEntity.ok("Account activated successfully");
    }
}
