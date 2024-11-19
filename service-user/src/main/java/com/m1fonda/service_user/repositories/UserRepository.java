package com.m1fonda.service_user.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
// import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import com.m1fonda.service_user.entities.Users;

@Repository
// @RepositoryRestResource
public interface UserRepository extends MongoRepository<Users, Long> {
    public Optional<Users> findByEmail(String email);
}
