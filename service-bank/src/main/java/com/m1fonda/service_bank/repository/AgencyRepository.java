package com.m1fonda.service_bank.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.m1fonda.service_bank.model.Agence;
import java.util.List;
import java.util.Optional;

@Repository
public interface AgencyRepository extends MongoRepository<Agence, String> {
    Optional<Agence> findByNumAgency(String numAgency);

    List<Agence> findByNumBank(String numBank);

    void deleteByNumAgency(String numAgency);
}
