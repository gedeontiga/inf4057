package com.m1fonda.service_user.services;

import org.springframework.stereotype.Service;

import com.m1fonda.commons_libs.dto.UserRequest;
import com.m1fonda.service_user.dto.UserResponse;
import com.m1fonda.service_user.entities.Role;
import com.m1fonda.service_user.entities.RoleType;
import com.m1fonda.service_user.entities.Users;
import com.m1fonda.service_user.repositories.RoleRepository;
import com.m1fonda.service_user.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserResponse createManager(UserRequest request) {
        Role role = new Role(RoleType.MANAGER);
        roleRepository.save(role);
        Users user = Users.builder()
                .cni(request.cni())
                .email(request.email())
                .lastName(request.lastName())
                .firstName(request.firstName())
                .password(request.password())
                .phoneNumber(request.phoneNumber())
                .role(role)
                .build();
        return UserResponse.fromUser(userRepository.save(user));
    }

    public void deleteManager(String email) {
        Users user = userRepository.findByEmail(email).orElseThrow();
        userRepository.delete(user);
    }
}
