package com.m1fonda.service_bank.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.m1fonda.service_bank.model.BankRequest;

@Repository
public interface BankRequestRepository extends MongoRepository<BankRequest, Long> {

}
