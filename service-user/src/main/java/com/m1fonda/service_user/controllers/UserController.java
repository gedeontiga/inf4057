package com.m1fonda.service_user.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.m1fonda.commons_libs.dto.UserRequest;
import com.m1fonda.service_user.dto.UserResponse;
import com.m1fonda.service_user.entities.Users;
import com.m1fonda.service_user.services.UserService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/update")
    public ResponseEntity<UserResponse> update(@RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.update(request));
    }

    @GetMapping("/read")
    public ResponseEntity<UserResponse> getUserByEmail() {
        Users user = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("==================>" + user.getFirstName());
        return ResponseEntity.ok(userService.read(user.getEmail()));
    }
}
