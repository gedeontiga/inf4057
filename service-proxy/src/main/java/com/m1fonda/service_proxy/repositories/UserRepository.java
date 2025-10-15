package com.m1fonda.service_proxy.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import com.m1fonda.service_proxy.entities.Users;

@Repository
// @RepositoryRestResource
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);

    void deleteByEmail(String email);

    Optional<Users> findByEmailAndEnabledIsTrue(String email);
}
