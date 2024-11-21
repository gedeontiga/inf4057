package com.m1fonda.commons_libs.dto;

import com.m1fonda.commons_libs.entities.Agency;

public record AgencyDTO(
                String numAgency,
                String name,
                double capital,
                double depositBankRate,
                double withdrawalBankRate,
                String address) {
        public static AgencyDTO fromAgency(Agency agency) {
                return new AgencyDTO(
                                agency.getNumAgency(),
                                agency.getName(),
                                agency.getCapital(),
                                agency.getDepositBankRate(),
                                agency.getWithdrawalBankRate(),
                                agency.getAddress());
        }
}
