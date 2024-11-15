package com.m1fonda.dto;

import com.m1fonda.entities.Account;

public record DepositResponse(Long id, double amount, Account account) {

}
