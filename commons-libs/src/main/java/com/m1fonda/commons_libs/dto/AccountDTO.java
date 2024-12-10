package com.m1fonda.commons_libs.dto;

import java.util.Date;

import com.m1fonda.commons_libs.entities.Account;

import lombok.Builder;

@Builder
public record AccountDTO(
                String numAccount,
                String numAgency,
                Double balance,
                Date createAt,
                String status) {
        public static AccountDTO fromAccount(Account account) {
                return new AccountDTO(
                                account.getNumAccount(),
                                account.getNumAgency(),
                                account.getBalance(),
                                account.getCreateAt(),
                                account.getStatus().toString());
        }
}
