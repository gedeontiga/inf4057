package com.m1fonda.service_bank.dto;

import java.util.Date;
import java.util.Set;

import com.m1fonda.service_bank.model.AgencyModel;
import com.m1fonda.service_bank.model.BankModel;

public record BankDTOResponse(
                String name,
                String logo,
                String ownerEmail,
                String type,
                double capital,
                String contact,
                String bankNumber,
                Set<AgencyModel> agencies,
                double withdrawFee,
                double transferFee,
                Date createdAt) {

        public static BankDTOResponse fromBank(BankModel b){
            return new BankDTOResponse(
                b.getName(),
                b.getLogo(),
                b.getOwnerEmail(),
                b.getType(),
                b.getCapital(),
                b.getContact(),
                b.getBankNumber(),
                b.getAgencies(),
                b.getWithdrawFee(),
                b.getTransferFee(),
                b.getDateCreation()
            );
        }

}
