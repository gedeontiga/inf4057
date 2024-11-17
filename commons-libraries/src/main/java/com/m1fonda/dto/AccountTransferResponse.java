package com.m1fonda.dto;

import java.util.Date;

public record AccountTransferResponse(String senderAccountNumber, String receiverAccountNumber, String senderUserName, String receiverUserName, String senderEmail, String receiverEmail, double transactionAmount, double newBalanceSender, double newBalanceReceiver, Date createdAt) {

}
