package com.m1fonda.commons_libs.dto;

import java.util.Date;

public record AccountDepositWithdrawalResponse(String transactionID, String status, String numeroCompte, String userName, String agencyID, String email,
        double transactionAmount, double fees, double newBalance, Date createdAt) {

}
