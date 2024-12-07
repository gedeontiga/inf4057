package com.m1fonda.service_bank.dto;

public record BankDTO(
                String name,
                String logo,
                String ownerEmail,
                String type,
                double capital,
                String contact,
                double withdrawFee,
                double transferFee,
                double externalFee) {


}
