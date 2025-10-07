package com.m1fonda.service_account.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.m1fonda.service_account.entities.Compte;
import com.m1fonda.service_account.entities.Users;

@Repository
public interface CompteRepository extends MongoRepository<Compte, String> {
    List<Compte> findByUser(Users user);

    long countByNumAgency(String numAgency);

    Optional<Compte> findByNumAgency(String numAgency);

    long countByNumBank(String numBank);

    Optional<Compte> findByNumAccount(String numAccount);
}
