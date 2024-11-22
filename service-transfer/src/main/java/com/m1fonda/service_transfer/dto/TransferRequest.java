package com.m1fonda.service_transfer.dto;

public record TransferRequest(String senderAccountNum, String receiverAccountNum, double amount) {

}
