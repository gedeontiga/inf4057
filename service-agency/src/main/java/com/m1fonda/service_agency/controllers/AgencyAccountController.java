package com.m1fonda.service_agency.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.m1fonda.service_agency.entities.Demande;
import com.m1fonda.service_agency.services.AgencyAccountService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/demande")
@AllArgsConstructor
public class AgencyAccountController {

    private final AgencyAccountService agencyAccountService;

    @PostMapping
    public ResponseEntity<Demande> creerDemande(@RequestBody Demande demande) {
        return ResponseEntity.ok(agencyAccountService.processDemande(demande));
    }
}
