package com.m1fonda.service_bank.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.m1fonda.service_bank.dto.AgencyDTO;
import com.m1fonda.service_bank.dto.BankDTO;
import com.m1fonda.service_bank.dto.BankDTOResponse;
import com.m1fonda.service_bank.service.BankService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/owner-bank")
@AllArgsConstructor
public class OwnerBankController {

    private final BankService bankService;

    @PostMapping("/new")
    public ResponseEntity<BankDTOResponse> newBank(@RequestBody BankDTO bank) {
        return ResponseEntity.ok(bankService.createBank(bank));
    }

    @PutMapping("/agency/update")
    public ResponseEntity<AgencyDTO> updateAgency(@RequestBody AgencyDTO entity) {
        return ResponseEntity.ok(bankService.updateAgency(entity));
    }

    @PostMapping("/agency/add")
    public ResponseEntity<BankDTOResponse> addAgency(@RequestBody AgencyDTO agency) {
        return ResponseEntity.ok(bankService.addAgency(agency));
    }

    @PostMapping("/agency/remove/{agencyNum}")
    public void removeAgency(@PathVariable String agencyNum) {
        bankService.removeAgency(agencyNum);
    }

    @PostMapping("/delete-all")
    public ResponseEntity<Object> deleteAll() {
        bankService.deleteAll();
        return ResponseEntity.ok(null);
    }
}
