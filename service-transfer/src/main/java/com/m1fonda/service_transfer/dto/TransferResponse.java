package com.m1fonda.service_transfer.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.m1fonda.service_transfer.model.Transfer;

public record TransferResponse(
        String senderAccountNum,
        String receiverAccountNum,
        String agencyNum,
        String transactionNumber,
        double newBalance,
        double fees,
        Date date) {
    public static TransferResponse fromTransfer(Transfer d) {
        return new TransferResponse(d.getSenderAccountNum(), d.getReceiverAccountNum(), d.getAgencyNum(),
                d.getTransactionNum(), d.getAmount(), d.getFees(), d.getCreatedAt());
    }

    public static List<TransferResponse> fromList(List<Transfer> ws) {
        List<TransferResponse> withdraws = new ArrayList<>();
        for (Transfer w : ws) {
            withdraws.add(TransferResponse.fromTransfer(w));
        }
        return withdraws;
    }
}
