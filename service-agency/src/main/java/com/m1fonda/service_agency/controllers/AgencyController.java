package com.m1fonda.service_agency.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.m1fonda.commons_libs.dto.AgencyDTO;
import com.m1fonda.service_agency.dto.DemandeDTO;
import com.m1fonda.service_agency.dto.ManagerDTO;
import com.m1fonda.service_agency.services.AgencyAccountService;
import com.m1fonda.service_agency.services.AgencyService;

import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@AllArgsConstructor
@RequestMapping("/api/agency")
public class AgencyController {
    private final AgencyService agencyService;
    private final AgencyAccountService accountService;

    @PreAuthorize("hasAnyRole('MANAGER', 'OWNER')")
    @GetMapping("/get-agency/{numAgency}")
    public ResponseEntity<AgencyDTO> getAgency(@PathVariable String numAgency) throws Exception {
        return ResponseEntity.ok(agencyService.getAgency(numAgency));
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'OWNER')")
    @GetMapping("/all-demands")
    public ResponseEntity<List<DemandeDTO>> getAllDemands() {
        return ResponseEntity.ok(accountService.getDemandes());
    }

    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/all-managers/{numAgency}")
    public ResponseEntity<Set<ManagerDTO>> getAllManager(@PathVariable String numAgency) {
        return ResponseEntity.ok(agencyService.getAllManagers(numAgency));
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'OWNER')")
    @PostMapping("/approve-demand/{demandeId}")
    public ResponseEntity<String> approveDemand(@PathVariable String demandeId) throws Exception {
        accountService.validerDemande(demandeId);
        return ResponseEntity.ok("APPROVED");
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'OWNER')")
    @PostMapping("/reject-demand/{demandeId}")
    public ResponseEntity<String> rejectDemand(@PathVariable String demandeId) {
        accountService.rejeterDemande(demandeId);
        return ResponseEntity.ok("REJECTED");
    }

    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/get-agencies/{numBank}")
    public ResponseEntity<Set<AgencyDTO>> getAllAgencies(@PathVariable String numBank) {
        return ResponseEntity.ok(agencyService.getAllAgencies(numBank));
    }

    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/update")
    public ResponseEntity<AgencyDTO> updateAgency(@RequestBody AgencyDTO agency) {
        return ResponseEntity.ok(agencyService.updateAgencyInfo(agency));
    }

}
