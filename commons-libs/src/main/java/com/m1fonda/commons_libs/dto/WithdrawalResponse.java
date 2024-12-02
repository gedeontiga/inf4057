package com.m1fonda.commons_libs.dto;

import java.util.Date;

public record WithdrawalResponse(String accountNum, String transaction, double amount, String status, Date createAt) {

}
