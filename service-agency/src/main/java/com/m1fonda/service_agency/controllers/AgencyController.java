package com.m1fonda.service_agency.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.m1fonda.service_agency.entities.Agence;
import com.m1fonda.service_agency.services.AgencyService;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@AllArgsConstructor
@RequestMapping("/api/agency")
public class AgencyController {

    private final AgencyService agencyService;

    @GetMapping("/{code}")
    public ResponseEntity<Agence> getAgency(@PathVariable Long code) {
        return ResponseEntity.ok(agencyService.getAgency(code));
    }
}
