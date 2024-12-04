package com.m1fonda.service_transfer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


@Document( collection = "transfers")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transfer {

    @Id
    private String id;

    @Indexed( unique = true)
    private String transactionNum;

    private String agencyNum;

    private double amount;

    private String senderAccountNum;

    private String receiverAccountNum;

    private double fees;

    @CreatedDate
    private Date createdAt ;
}
