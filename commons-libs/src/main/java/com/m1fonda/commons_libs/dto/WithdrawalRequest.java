package com.m1fonda.commons_libs.dto;


public record WithdrawalRequest(String accountNum, String agencyNum, double amount) {

}
