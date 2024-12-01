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

    // public BankDTOResponse(String string, String string2, String string3, String string4, double d, String string5,
    //         String string6, LocalDateTime now) {
    //     //TODO Auto-generated constructor stub
    // }

}
