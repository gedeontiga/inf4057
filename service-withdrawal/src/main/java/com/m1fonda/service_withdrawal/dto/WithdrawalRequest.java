package com.m1fonda.service_withdrawal.dto;

public record WithdrawalRequest(String accountNum, String agencyNum, double amount, double fees) {

}
