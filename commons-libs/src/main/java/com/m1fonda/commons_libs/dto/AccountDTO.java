package com.m1fonda.commons_libs.dto;

import java.util.Date;

import com.m1fonda.commons_libs.entities.Account;

import lombok.Builder;

@Builder
public record AccountDTO(
                String numAccount,
                String numAgency,
                Double balance,
                Double fees,
                Date createAt,
                String status) {
        public static AccountDTO fromAccount(Account account, Double fees) {
                return new AccountDTO(
                                account.getNumAccount(),
                                account.getNumAgency(),
                                account.getBalance(),
                                fees,
                                account.getCreateAt(),
                                account.getStatus().toString());
        }
}
