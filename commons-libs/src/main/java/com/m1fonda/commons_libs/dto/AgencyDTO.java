package com.m1fonda.commons_libs.dto;

import com.m1fonda.commons_libs.entities.Agency;

import lombok.Builder;

@Builder
public record AgencyDTO(
                String numAgency,
                String name,
                double capital,
                String address,
                String numBank) {
        public static AgencyDTO fromAgency(Agency agency) {
                return new AgencyDTO(
                                agency.getNumAgency(),
                                agency.getName(),
                                agency.getCapital(),
                                agency.getAddress(),
                                agency.getNumBank());
        }
}
