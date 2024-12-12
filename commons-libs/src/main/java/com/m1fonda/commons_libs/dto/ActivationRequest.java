package com.m1fonda.commons_libs.dto;

import lombok.Builder;

@Builder
public record ActivationRequest(
        String email,
        Long activationCode) {
}
