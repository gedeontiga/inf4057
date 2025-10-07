package com.m1fonda.commons_libs.dto;

import java.util.Date;

public record TransferRequest(
                            String senderAccountNum,
                            String senderAgency,
                            String receiverAccountNum,
                            String receiverAgency,
                            String transactionNumber,
                            String senderEmail,
                            String receiverEmail,
                            String senderName,
                            String receiverName,
                            String status,
                            double amount,
                            double fees,
                            double senderNewBalance,
                            double receiverNewBalance,
                            Date date
                            )
{}
