package com.m1fonda.service_transfer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.m1fonda.service_transfer.model.Transfer;

@Repository
public interface TransferRepository extends MongoRepository<Transfer, String> {
    Optional<Transfer> findByTransactionNum(String id);

    @Query("{$and: [?0]}")
    List<Transfer> findByDynamicCriteria(List<Criteria> criteria);
}
