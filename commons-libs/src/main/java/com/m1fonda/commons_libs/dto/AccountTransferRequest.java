package com.m1fonda.commons_libs.dto;


public record AccountTransferRequest(
                            String senderAccountNum,
                            String receiverAccountNum,
                            double amount,
                            double fees,
                            double senderNewBalance,
                            double receiverNewBalance
                            )
{}
