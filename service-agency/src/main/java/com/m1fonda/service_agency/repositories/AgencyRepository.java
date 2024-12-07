package com.m1fonda.service_agency.repositories;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.m1fonda.service_agency.entities.Agence;

@Repository
public interface AgencyRepository extends MongoRepository<Agence, String> {
    Optional<Agence> findById(Long id);

    Optional<Agence> findByNumAgency(String numAgency);

    Optional<Agence> findByNumAgencyAndNumBank(String numAgency, String numBank);

    Optional<Agence> findByAddressAndNumAgency(String address, String numAgency);

    void deleteByNumAgency(String numAgency);

    Set<Agence> findAllByNumBank(String numBank);
}
