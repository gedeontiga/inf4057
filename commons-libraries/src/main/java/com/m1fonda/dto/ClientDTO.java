package com.m1fonda.dto;

public record ClientDTO(
        String cni,
        String firstName,
        String lastName,
        Long phoneNumber,
        AccountDTO account) {
}
