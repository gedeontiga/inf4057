package com.m1fonda.dto;

import com.m1fonda.entities.Account;

public record TransferRequest(Account senderAccount, Account receiverAccount, double amount) {

}
