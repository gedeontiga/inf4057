package com.m1fonda.service_transfer.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.m1fonda.service_transfer.model.Transfer;

@Repository
public interface TransferRepository extends MongoRepository<Transfer, String> {
    List<Transfer> findByAgencyNum(String agencyNum);
    List<Transfer> findBySenderAccountNumOrReceiverAccountNumAndAgencyNum(String accountNum, String agencyNum);
    List<Transfer> findBySenderAccountNumOrReceiverAccountNum(String account);

}
