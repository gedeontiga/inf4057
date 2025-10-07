package com.m1fonda.service_user.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.m1fonda.commons_libs.dto.UserRequest;
import com.m1fonda.service_user.dto.ManagerCreationRequest;
import com.m1fonda.service_user.dto.UserResponse;
import com.m1fonda.service_user.services.OwnerService;
import com.m1fonda.service_user.services.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/owner")
public class OwnerController {

    private final UserService userService;
    private final OwnerService ownerService;

    @PostMapping("/update")
    public ResponseEntity<UserResponse> update(@RequestBody UserRequest owner) {
        return ResponseEntity.ok(userService.update(owner));
    }

    @PostMapping("/delete-manager")
    public ResponseEntity<Void> deleteManager(@RequestBody String email) {
        ownerService.deleteManager(email);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/create-manager")
    public ResponseEntity<UserResponse> createManager(@RequestBody ManagerCreationRequest request) {
        return ResponseEntity.ok(ownerService.createManager(request));
    }
}
