package com.m1fonda.service_withdrawal.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.m1fonda.service_withdrawal.model.Withdrawal;

@Repository
public interface WithdrawalRepository extends MongoRepository<Withdrawal, String> {

}
