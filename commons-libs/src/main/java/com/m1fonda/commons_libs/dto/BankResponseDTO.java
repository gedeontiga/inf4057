package com.m1fonda.commons_libs.dto;

public record BankResponseDTO(
                String name,
                String logo,
                double sendFees,
                double withdrawalBankFees) {
}
