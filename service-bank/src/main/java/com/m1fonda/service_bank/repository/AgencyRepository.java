package com.m1fonda.service_bank.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.m1fonda.service_bank.model.AgencyModel;


@Repository
public interface AgencyRepository extends MongoRepository<AgencyModel, String> {
    Optional<AgencyModel> findByNumAgency(String numAgency);
    void deleteByNumAgency(String numAgency);
    // void deleteByNumAgency(String numAgency);
}
