package com.m1fonda.service_deposit.dto;

import java.util.Date;

import com.m1fonda.service_deposit.model.Deposit;

public record NotificationResponse(String transactionId, String accountId, double amount, Date date) {
    public static NotificationResponse fromDeposit(Deposit d) {
        return new NotificationResponse(
            d.getTransactionNum(),
            d.getAccountNum(),
            d.getAmount(),
            d.getCreatedAt()
        );
    }
}
