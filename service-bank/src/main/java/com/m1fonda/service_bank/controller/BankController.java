package com.m1fonda.service_bank.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.m1fonda.service_bank.dto.AgencyDTO;
import com.m1fonda.service_bank.dto.BankDTO;
import com.m1fonda.service_bank.dto.BankDTOResponse;
import com.m1fonda.service_bank.dto.BankWithAgenciesDTO;
import com.m1fonda.service_bank.service.BankService;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;




@AllArgsConstructor
@RestController
@RequestMapping("/api/bank")
public class BankController {

    private final BankService bankService;

    @PostMapping("/new")
    public ResponseEntity<BankDTOResponse> newBank(@RequestBody BankDTO bank) {
        return ResponseEntity.ok(bankService.createBank(bank));
    }

    @GetMapping("/get/{param}")
    public ResponseEntity<BankWithAgenciesDTO> getBank(@RequestParam String param) {
        return ResponseEntity.ok(bankService.getBank(param));
    }
    
    @PutMapping("/update")
    public ResponseEntity<AgencyDTO> updateAgency(@RequestBody AgencyDTO entity) {
        return ResponseEntity.ok(bankService.updateAgency(entity));
    }

    @PostMapping("/agency/add")
    public ResponseEntity<BankWithAgenciesDTO> addAgency(@RequestBody AgencyDTO agency) {
        return ResponseEntity.ok(bankService.addAgency(agency));
    }

    @PostMapping("/agency/remove")
    public boolean removeAgency(@RequestBody AgencyDTO agency) {
        return bankService.removeAgency(agency);
    }
    
}
