package com.m1fonda.service_account.dto;

import com.m1fonda.service_account.entities.Compte;

import lombok.Builder;

@Builder
public record AccountUpdateDTO(
                String numAccount,
                String numAgency,
                Double balance,
                String status) {
        public static AccountUpdateDTO fromAccount(Compte account) {
                return new AccountUpdateDTO(
                                account.getNumAccount(),
                                account.getNumAgency(),
                                account.getBalance(),
                                account.getStatus().name());
        }
}
