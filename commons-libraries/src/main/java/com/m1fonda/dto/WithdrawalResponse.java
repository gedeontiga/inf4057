package com.m1fonda.dto;

import com.m1fonda.entities.Account;

public record WithdrawalResponse(Long id, double amount, Account account) {

}
