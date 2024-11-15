package com.m1fonda.dto;

public record AgencyDTO(
        String agencyNumber,
        String name,
        double capital,
        double depositBankRate,
        double withdrawalBankRate,
        String address) {
}
