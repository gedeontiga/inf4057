package com.m1fonda.dto;

import java.util.Date;

public record AccountDTO(
                String accountNumber,
                Double balance,
                Date createAt) {
}
