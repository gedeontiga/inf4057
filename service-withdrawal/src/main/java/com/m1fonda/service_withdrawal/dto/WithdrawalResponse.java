package com.m1fonda.service_withdrawal.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.m1fonda.service_withdrawal.model.Withdrawal;


public record WithdrawalResponse(String accountNum, String agencyNum, String transaction, double amount, double fees, Date createAt) {

    public static WithdrawalResponse fromWithdrawal(Withdrawal w){
            return new WithdrawalResponse(
                w.getAccountNum(),
                w.getAgencyNum(),
                w.getTransactionNum(),
                w.getAmount(),
                w.getFees(),
                w.getCreatedAt()
            );
            
    }
    public static List<WithdrawalResponse> fromList(List<Withdrawal> ws){
        List<WithdrawalResponse> withdraws = new ArrayList<>();
        for (Withdrawal w: ws){
            withdraws.add(WithdrawalResponse.fromWithdrawal(w));
        }
        return withdraws;
    }
}
