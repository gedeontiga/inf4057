package com.m1fonda.service_user.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.m1fonda.commons_libs.dto.UserRequest;
import com.m1fonda.service_user.dto.UserResponse;
import com.m1fonda.service_user.services.UserService;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.http.ResponseEntity;

@RestController
@AllArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/update")
    public ResponseEntity<UserResponse> update(@RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.update(request));
    }

    @PostMapping("/read")
    public ResponseEntity<UserResponse> getUserByEmail(@RequestBody String email) {
        return ResponseEntity.ok(userService.read(email));
    }
}
