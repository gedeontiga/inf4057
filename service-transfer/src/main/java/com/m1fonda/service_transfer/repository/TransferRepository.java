package com.m1fonda.service_transfer.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.m1fonda.service_transfer.model.Transfer;

@Repository
public interface TransferRepository extends MongoRepository<Transfer, String> {
    Optional<Transfer> findByTransactionNum(String id);
}
