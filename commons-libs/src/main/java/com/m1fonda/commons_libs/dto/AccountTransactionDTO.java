package com.m1fonda.commons_libs.dto;

import com.m1fonda.commons_libs.entities.Account;

import lombok.Builder;

@Builder
public record AccountTransactionDTO(
        String numAccount,
        String numAgency,
        String userEmail,
        Double balance) {
    public static AccountTransactionDTO fromAccount(Account account, String userEmail) {
        return new AccountTransactionDTO(
                account.getNumAccount(),
                account.getNumAgency(),
                userEmail,
                account.getBalance());
    }
}
