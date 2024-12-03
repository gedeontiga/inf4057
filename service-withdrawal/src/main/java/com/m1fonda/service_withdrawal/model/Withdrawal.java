package com.m1fonda.service_withdrawal.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


@Document( collection = "withdrawals")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Withdrawal {

    @Id
    private String id;

    @Indexed(unique = true)
    private String transactionNum;

    private String agencyNum;

    private double amount;

    private String accountNum;

    private double fees;

    @CreatedDate
    private final Date createdAt = new Date();
}
