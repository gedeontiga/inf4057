package com.m1fonda.commons_libs.dto;

import com.m1fonda.commons_libs.entities.Account;

import lombok.Builder;

@Builder
public record AccountDTO(
                String numAccount,
                String numAgency,
                Double balance) {
        public static AccountDTO fromAccount(Account account) {
                return new AccountDTO(
                                account.getNumAccount(),
                                account.getNumAgency(),
                                account.getBalance());
        }
}
