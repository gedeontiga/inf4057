package com.m1fonda.service_agency.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.m1fonda.service_agency.entities.Demande;
import java.util.List;
import java.util.Optional;

@Repository
public interface DemandeRepository extends MongoRepository<Demande, String> {
    List<Demande> findByStatus(String status);

    Optional<Demande> findByEmailAndNumBank(String email, String numBank);

    Optional<Demande> findById(String id);
}
