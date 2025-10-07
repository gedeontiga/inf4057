package com.m1fonda.service_user.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.m1fonda.commons_libs.dto.UserRequest;
import com.m1fonda.service_user.dto.UserResponse;
import com.m1fonda.service_user.services.AdminService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/delete-owner")
    public ResponseEntity<Void> deleteManager(@RequestBody String email) {
        adminService.deleteOwner(email);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/create-owner")
    public ResponseEntity<UserResponse> createManager(@RequestBody UserRequest request) {
        return ResponseEntity.ok(adminService.createOwner(request));
    }
}
