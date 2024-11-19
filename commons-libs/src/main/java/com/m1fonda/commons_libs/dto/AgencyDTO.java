package com.m1fonda.commons_libs.dto;

public record AgencyDTO(
        String numAgency,
        String name,
        double capital,
        double depositBankRate,
        double withdrawalBankRate,
        String address) {
}
