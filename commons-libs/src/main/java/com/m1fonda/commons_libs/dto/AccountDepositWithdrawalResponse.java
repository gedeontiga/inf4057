package com.m1fonda.commons_libs.dto;

import java.util.Date;

public record AccountDepositWithdrawalResponse(String numeroCompte, String userName, String email,
        double transactionAmount, double newBalance, Date createdAt) {

}
