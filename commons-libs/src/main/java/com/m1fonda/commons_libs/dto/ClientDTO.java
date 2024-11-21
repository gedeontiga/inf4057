package com.m1fonda.commons_libs.dto;

public record ClientDTO(
                String cni,
                String firstName,
                String lastName,
                Long phoneNumber,
                String password,
                String address) {
}
