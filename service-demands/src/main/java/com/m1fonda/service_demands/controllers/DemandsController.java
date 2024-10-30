package com.m1fonda.service_demands.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.m1fonda.entities.Demand;
import com.m1fonda.service_demands.services.DemandeService;

@RestController
@RequestMapping("/demande")
public class DemandsController {
    private final DemandeService demandeService;

    public DemandsController(DemandeService demandeService) {
        this.demandeService = demandeService;
    }

    @PostMapping
    public ResponseEntity<String> creerDemande(@RequestBody Demand demande) {
        demandeService.envoyerDemande(demande);
        return ResponseEntity.ok("Demande envoyée pour traitement.");
    }
}
