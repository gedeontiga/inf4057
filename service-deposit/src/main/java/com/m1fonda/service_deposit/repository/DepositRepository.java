package com.m1fonda.service_deposit.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.m1fonda.service_deposit.model.Deposit;

@Repository
public interface DepositRepository extends MongoRepository<Deposit, Long> {

}
