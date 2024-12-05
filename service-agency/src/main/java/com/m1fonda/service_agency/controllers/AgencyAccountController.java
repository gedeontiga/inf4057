package com.m1fonda.service_agency.controllers;

import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.m1fonda.commons_libs.dto.AgencyDTO;
import com.m1fonda.service_agency.entities.Demande;
import com.m1fonda.service_agency.services.AgencyAccountService;
import com.m1fonda.service_agency.services.AgencyService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/demande")
@AllArgsConstructor
public class AgencyAccountController {

    private final AgencyAccountService agencyAccountService;
    private final AgencyService agencyService;

    @PostMapping
    public ResponseEntity<String> creerDemande(@RequestBody Demande demande) {
        agencyAccountService.sendDemande(demande);
        return ResponseEntity.ok("Success");
    }

    @GetMapping("/get-agency/{numAgency}")
    public ResponseEntity<AgencyDTO> getAgency(@PathVariable String numAgency) throws Exception {
        return ResponseEntity.ok(agencyService.getAgency(numAgency));
    }

    @GetMapping("/get-agencies/{numBank}")
    public ResponseEntity<Set<AgencyDTO>> getAllAgencies(@PathVariable String numBank) {
        return ResponseEntity.ok(agencyService.getAllAgencies(numBank));
    }
}
