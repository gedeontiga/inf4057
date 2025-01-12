package com.m1fonda.service_agency.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.m1fonda.service_agency.entities.Managers;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ManagersRepository extends MongoRepository<Managers, String> {
    Optional<Managers> findByEmail(String email);

    Set<Managers> findByNumAgency(String numAgency);
}
