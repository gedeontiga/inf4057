package com.m1fonda.service_auth.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.m1fonda.service_auth.entities.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
