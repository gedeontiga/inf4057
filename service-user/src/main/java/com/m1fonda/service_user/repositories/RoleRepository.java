package com.m1fonda.service_user.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.m1fonda.service_user.entities.Role;
import com.m1fonda.service_user.entities.RoleType;

@Repository
public interface RoleRepository extends MongoRepository<Role, String> {
    Optional<Role> findByType(RoleType type);
}
