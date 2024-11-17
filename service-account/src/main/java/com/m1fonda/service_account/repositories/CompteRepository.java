package com.m1fonda.service_account.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.m1fonda.service_account.entities.Compte;

@Repository
public interface CompteRepository extends MongoRepository<Compte, Long> {

}
