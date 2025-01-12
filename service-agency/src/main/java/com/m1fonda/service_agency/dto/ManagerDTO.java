package com.m1fonda.service_agency.dto;

import com.m1fonda.service_agency.entities.Managers;

public record ManagerDTO(
                String id,
                String email,
                String numAgency) {
        public static ManagerDTO managerFactory(Managers managers) {
                return new ManagerDTO(managers.getId(), managers.getEmail(), managers.getNumAgency());
        }
}
