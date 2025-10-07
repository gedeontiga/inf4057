package com.m1fonda.commons_libs.dto;

public record AccountResponseTransferDTO(
        String numBankSender,
        String numBankReceiver,
        String emailReceiver) {
}
