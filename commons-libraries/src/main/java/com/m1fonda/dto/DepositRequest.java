package com.m1fonda.dto;

import com.m1fonda.entities.Account;

public record DepositRequest(double amount, Account account) {

}
