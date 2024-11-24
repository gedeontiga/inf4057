package com.m1fonda.service_auth.repositories;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.m1fonda.service_auth.entities.Jwt;
import com.m1fonda.service_auth.entities.Users;

@Repository
public interface JwtRepository extends JpaRepository<Jwt, Long> {
    Optional<Jwt> findByToken(String token);

    void deleteAllByExpiredIsBefore(Instant instant);

    Optional<Jwt> findByUserAndExpiredIsAfter(Users user, Instant now);
}
