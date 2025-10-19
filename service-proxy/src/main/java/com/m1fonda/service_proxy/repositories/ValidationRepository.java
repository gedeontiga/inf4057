package com.m1fonda.service_proxy.repositories;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.m1fonda.service_proxy.entities.Validation;

@Repository
public interface ValidationRepository extends JpaRepository<Validation, Long> {

    Optional<Validation> findByActivationCodeAndExpiredAfter(Long code, Instant now);

    Optional<Validation> findByEmail(String email);

    void deleteByExpiredBefore(Instant now);
}
