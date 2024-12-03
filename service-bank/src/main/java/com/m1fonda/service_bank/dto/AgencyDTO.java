package com.m1fonda.service_bank.dto;

import com.m1fonda.service_bank.model.AgencyModel;

public record AgencyDTO(
                String numAgency,
                String name,
                double capital,
                double depositBankRate,
                double withdrawalBankRate,
                String address,
                String numBank) {
        public static AgencyDTO fromAgency(AgencyModel model) {
            return new AgencyDTO(model.getNumAgency(), model.getName(), model.getCapital(), model.getDepositBankRate(), model.getWithdrawalBankRate(), model.getAddress(), model.getNumBank());
        }
}
