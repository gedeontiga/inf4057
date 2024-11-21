package com.m1fonda.service_user.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.m1fonda.commons_libs.dto.UserRequest;
import com.m1fonda.service_user.dto.UserResponse;
import com.m1fonda.service_user.services.AdminService;
import com.m1fonda.service_user.services.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;
    private final AdminService adminService;

    @PostMapping("/update")
    public ResponseEntity<UserResponse> update(@RequestBody UserRequest admin) {
        return ResponseEntity.ok(userService.update(admin));
    }

    @PostMapping("/delete-manager")
    public ResponseEntity<Void> deleteManager(@RequestBody String email) {
        adminService.deleteManager(email);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/create-manager")
    public ResponseEntity<UserResponse> createManager(@RequestBody UserRequest request) {
        return ResponseEntity.ok(adminService.createManager(request));
    }
}
