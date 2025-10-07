package com.m1fonda.service_deposit.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.m1fonda.service_deposit.model.Deposit;

@Repository
public interface DepositRepository extends MongoRepository<Deposit, String> {
    List<Deposit> findByAgencyNum(String agencyNum);
    List<Deposit> findByAccountNum(String accountNum);
    List<Deposit> findByAgencyNumAndAccountNum(String agencyNum, String accountNum);

}
