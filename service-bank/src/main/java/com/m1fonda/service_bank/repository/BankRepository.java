package com.m1fonda.service_bank.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.m1fonda.service_bank.model.BankModel;

@Repository
public interface BankRepository extends MongoRepository<BankModel, String> {
    Optional<BankModel> findByBankNumber(String bankNumber);

    List<BankModel> findByName(String name);
}
