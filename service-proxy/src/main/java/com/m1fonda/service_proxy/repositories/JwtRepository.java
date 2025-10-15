package com.m1fonda.service_proxy.repositories;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.m1fonda.service_proxy.entities.Jwt;
import com.m1fonda.service_proxy.entities.Users;

@Repository
public interface JwtRepository extends JpaRepository<Jwt, Long> {
    Optional<Jwt> findByToken(String token);

    void deleteAllByExpiredAtIsBefore(Instant instant);

    Optional<Jwt> findByUserAndExpiredAtIsAfter(Users user, Instant now);
}
