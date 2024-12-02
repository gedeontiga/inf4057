package com.m1fonda.service_bank.dto;

public record AgencyDTO(
                String numAgency,
                String name,
                double capital,
                double depositBankRate,
                double withdrawalBankRate,
                String address,
                String bankId) {

}
