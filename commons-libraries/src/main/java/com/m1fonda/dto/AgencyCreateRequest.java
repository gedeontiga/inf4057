package com.m1fonda.dto;

public record AgencyCreateRequest(String name, double capital, Long bankId, Long managerId, String address) {
    
}
