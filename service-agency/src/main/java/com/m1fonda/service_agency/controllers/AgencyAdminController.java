package com.m1fonda.service_agency.controllers;

import java.util.Set;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.m1fonda.commons_libs.dto.AgencyDTO;
import com.m1fonda.service_agency.services.AgencyService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/agencies")
public class AgencyAdminController {

    private final AgencyService agencyService;

    @GetMapping("/get-agencies/{numBank}")
    public Set<AgencyDTO> getAllAgencies(@PathVariable String numBank) {
        return agencyService.getAllAgencies(numBank);
    }
}
