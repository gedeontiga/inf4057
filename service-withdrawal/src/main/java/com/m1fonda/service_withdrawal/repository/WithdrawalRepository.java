package com.m1fonda.service_withdrawal.repository;

import java.util.List;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.m1fonda.service_withdrawal.model.Withdrawal;

@Repository
public interface WithdrawalRepository extends MongoRepository<Withdrawal, String> {

    @Query("{$and: [?0]}")
    List<Withdrawal> findByDynamicCriteria(List<Criteria> criteria);
}
