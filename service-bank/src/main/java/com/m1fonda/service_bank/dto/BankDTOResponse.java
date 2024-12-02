package com.m1fonda.service_bank.dto;

import java.util.Date;

public record BankDTOResponse(
        String name,
        String logo,
        String ownerEmail,
        String type,
        double capital,
        String contact,
        String bankNumber,
        Date createdAt) {

}
