package com.m1fonda.service_agency.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.m1fonda.commons_libs.dto.AgencyDTO;
import com.m1fonda.service_agency.services.AgencyService;

import lombok.AllArgsConstructor;

import java.util.Set;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@AllArgsConstructor
@RequestMapping("/api/agency")
public class AgencyAdminController {

    private final AgencyService agencyService;

    @PreAuthorize("hasAuthority('ADMIN_READ_ALL')")
    @GetMapping("/get-agencies/{bankId}")
    public Set<AgencyDTO> getAllAgencies(@PathVariable Long bankId) {
        return agencyService.getAllAgencies(bankId);
    }

}
