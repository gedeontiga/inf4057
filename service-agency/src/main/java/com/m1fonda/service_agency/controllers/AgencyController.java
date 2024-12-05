package com.m1fonda.service_agency.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.m1fonda.commons_libs.dto.AgencyDTO;
import com.m1fonda.service_agency.dto.DemandeDTO;
import com.m1fonda.service_agency.services.AgencyAccountService;
import com.m1fonda.service_agency.services.AgencyService;

import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@AllArgsConstructor
@RequestMapping("/api/agency")
public class AgencyController {
    private final AgencyService agencyService;
    private final AgencyAccountService accountService;

    @GetMapping("/get-agency/{numAgency}")
    public ResponseEntity<AgencyDTO> getAgency(@PathVariable String numAgency) throws Exception {
        return ResponseEntity.ok(agencyService.getAgency(numAgency));
    }

    @GetMapping("/all-demands")
    public ResponseEntity<List<DemandeDTO>> getAllDemands() {
        return ResponseEntity.ok(accountService.getDemandes());
    }

    @PostMapping("/approve-demand/{demandeId}")
    public ResponseEntity<String> approveDemand(@PathVariable String demandeId) throws Exception {
        accountService.validerDemande(demandeId);
        return ResponseEntity.ok("APPROVED");
    }

    @PostMapping("/reject-demand/{demandeId}")
    public ResponseEntity<String> rejectDemand(@PathVariable String demandeId) {
        accountService.rejeterDemande(demandeId);
        return ResponseEntity.ok("REJECTED");
    }

    @GetMapping("/count-client/{numAgency}")
    public ResponseEntity<Long> getClientNumber(@PathVariable String numAgency) throws Exception {
        return ResponseEntity.ok(agencyService.getClientNumber(numAgency));
    }

    @GetMapping("/get-agencies/{numBank}")
    public ResponseEntity<Set<AgencyDTO>> getAllAgencies(@PathVariable String numBank) {
        return ResponseEntity.ok(agencyService.getAllAgencies(numBank));
    }

}
