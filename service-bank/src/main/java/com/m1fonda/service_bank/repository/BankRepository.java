package com.m1fonda.service_bank.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.m1fonda.service_bank.model.Banque;

@Repository
public interface BankRepository extends MongoRepository<Banque, String> {
    Optional<Banque> findByBankNumber(String bankNumber);
}
