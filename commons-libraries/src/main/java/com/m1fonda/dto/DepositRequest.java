package com.m1fonda.dto;

import com.m1fonda.entities.Account;

public record DepositRequest(Account account, double amount) {

}
