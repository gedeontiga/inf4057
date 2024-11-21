package com.m1fonda.commons_libs.dto;

import com.m1fonda.commons_libs.entities.Account;

public record TransferRequest(Account senderAccount, Account receiverAccount, double amount) {

}
