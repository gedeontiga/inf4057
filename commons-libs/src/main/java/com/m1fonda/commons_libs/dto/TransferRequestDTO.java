package com.m1fonda.commons_libs.dto;


public record TransferRequestDTO(
                            String senderAccountNum,
                            String receiverAccountNum,
                            double amount
                            )
{}
