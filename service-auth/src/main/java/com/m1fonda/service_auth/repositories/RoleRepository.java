package com.m1fonda.service_auth.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.m1fonda.service_auth.entities.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
