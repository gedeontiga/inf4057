package com.m1fonda.service_deposit.repository;

import java.util.List;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.m1fonda.service_deposit.model.Deposit;

@Repository
public interface DepositRepository extends MongoRepository<Deposit, String> {
    @Query("{$and: [?0]}")
    List<Deposit> findByDynamicCriteria(List<Criteria> criteria);
}
