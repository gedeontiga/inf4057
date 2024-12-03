package com.m1fonda.service_deposit.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.m1fonda.service_deposit.model.Deposit;

public record DepositResponse(String accountNum, String transaction, double amount, Date createAt) {
    public static DepositResponse fromDeposit(Deposit d){
        return new DepositResponse(d.getAccountNum(), d.getTransactionNum(), d.getAmount(), d.getCreatedAt());
    }

    public static List<DepositResponse> fromList(List<Deposit> ws){
        List<DepositResponse> withdraws = new ArrayList<>();
        for (Deposit w: ws){
            withdraws.add(DepositResponse.fromDeposit(w));
        }
        return withdraws;
    }

}

