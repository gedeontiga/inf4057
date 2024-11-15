package com.m1fonda.dto;

import java.util.Date;

public record AnnounceDTO(
        String title,
        String description,
        Date createAt,
        String picture) {
}
