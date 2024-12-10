package com.m1fonda.commons_libs.dto;

import java.util.Date;

import lombok.Builder;

@Builder
public record AccountTransactionDTO(
                String numAccount,
                String numAgency,
                Double balance,
                Date createAt,
                String status,
                double fees) {
}
