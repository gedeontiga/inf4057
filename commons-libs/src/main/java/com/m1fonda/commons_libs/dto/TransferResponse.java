package com.m1fonda.commons_libs.dto;

import java.util.Date;

public record TransferResponse(
                            String senderAccountNum,
                            String receiverAccountNum,
                            String transactionNumber,
                            String status,
                            double amount,
                            Date date
                            )
{}
