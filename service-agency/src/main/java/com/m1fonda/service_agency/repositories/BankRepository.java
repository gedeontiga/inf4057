package com.m1fonda.service_agency.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.m1fonda.service_agency.entities.Banque;

public interface BankRepository extends MongoRepository<Banque, Long> {

}
