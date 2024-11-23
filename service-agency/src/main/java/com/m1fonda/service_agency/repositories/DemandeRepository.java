package com.m1fonda.service_agency.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.m1fonda.service_agency.entities.Demande;

@Repository
public interface DemandeRepository extends MongoRepository<Demande, String> {
}
