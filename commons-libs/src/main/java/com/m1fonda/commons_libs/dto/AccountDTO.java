package com.m1fonda.commons_libs.dto;

import java.util.Date;

public record AccountDTO(
        String accountNumber,
        Double balance,
        Date createAt) {
}
