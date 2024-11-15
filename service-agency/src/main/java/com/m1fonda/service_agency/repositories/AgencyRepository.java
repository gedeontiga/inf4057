package com.m1fonda.service_agency.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.m1fonda.service_agency.entities.Agence;

@Repository
public interface AgencyRepository extends MongoRepository<Agence, Long> {
    Optional<Agence> findById(Long id);

    Optional<Agence> findByNumAgency(String numAgency);
}
