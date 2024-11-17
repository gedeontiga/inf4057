package com.m1fonda.dto;

import com.m1fonda.entities.Account;

public record WithdrawalRequest(Account account, double amount) {

}
