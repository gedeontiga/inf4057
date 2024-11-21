package com.m1fonda.service_user.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.m1fonda.service_user.entities.Role;

@Repository
public interface RoleRepository extends MongoRepository<Role, Long> {
}
