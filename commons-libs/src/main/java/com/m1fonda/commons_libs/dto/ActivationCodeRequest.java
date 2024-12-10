package com.m1fonda.commons_libs.dto;

import java.io.Serializable;

import lombok.Builder;

@Builder
public record ActivationCodeRequest(
        String email,
        String firstName,
        Long code) implements Serializable {
}
