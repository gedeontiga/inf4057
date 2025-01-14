package com.m1fonda.service_account.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
// import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import com.m1fonda.service_account.entities.Users;

@Repository
// @RepositoryRestResource
public interface UserRepository extends MongoRepository<Users, String> {
    public Optional<Users> findByEmail(String email);
}
