package com.m1fonda.service_deposit.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.m1fonda.service_deposit.model.Compte;

@Repository
public interface CompteRepository extends MongoRepository<Compte, String> {
    List<Compte> findByUserEmail(String userEmail);

    Optional<Compte> findByNumAgency(String numAgency);

    Optional<Compte> findByNumAccount(String numAccount);
}
