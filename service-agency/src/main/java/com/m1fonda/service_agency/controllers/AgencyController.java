package com.m1fonda.service_agency.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.m1fonda.commons_libs.dto.AgencyDTO;
import com.m1fonda.service_agency.services.AgencyService;

import lombok.AllArgsConstructor;

import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@AllArgsConstructor
@RequestMapping("/api/agency")
public class AgencyController {

    private final AgencyService agencyService;

    @GetMapping("/get-agency/{numAgency}")
    public ResponseEntity<AgencyDTO> getAgency(@PathVariable String numAgency) {
        return ResponseEntity.ok(agencyService.getAgency(numAgency));
    }

    @GetMapping("/get-agencies/{numBank}")
    public Set<AgencyDTO> getAllAgencies(@PathVariable String numBank) {
        return agencyService.getAllAgencies(numBank);
    }
}
