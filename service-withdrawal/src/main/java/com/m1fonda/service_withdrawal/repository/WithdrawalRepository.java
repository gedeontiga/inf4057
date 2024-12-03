package com.m1fonda.service_withdrawal.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.m1fonda.service_withdrawal.model.Withdrawal;
import java.util.List;


@Repository
public interface WithdrawalRepository extends MongoRepository<Withdrawal, String> {
    List<Withdrawal> findByAgencyNum(String agencyNum);
    List<Withdrawal> findByAccountNum(String accountNum);
    List<Withdrawal> findByAgencyNumAndAccountNum(String agencyNum, String accountNum);
}
