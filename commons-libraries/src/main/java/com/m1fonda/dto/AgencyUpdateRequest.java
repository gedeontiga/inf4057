package com.m1fonda.dto;

public record AgencyUpdateRequest(Long agenyId, String name, double capital, Long bankId, Long managerId, String address) {
    
}
