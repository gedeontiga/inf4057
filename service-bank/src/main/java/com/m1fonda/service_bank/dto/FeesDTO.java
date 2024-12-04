package com.m1fonda.service_bank.dto;

import com.m1fonda.service_bank.model.BankModel;

public record FeesDTO(
                double withdrawFee,
                double transferFee) {

    public static FeesDTO fromBank(BankModel b){
        return new FeesDTO(b.getWithdrawFee(), b.getTransferFee());
    }

}
