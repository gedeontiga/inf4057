package com.m1fonda.service_bank.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.m1fonda.dto.AgencyCreateRequest;
import com.m1fonda.service_bank.model.BankModel;
import com.m1fonda.service_bank.service.BankService;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;



@AllArgsConstructor
@RestController
@RequestMapping("/api/bank")
public class BankController {

    private final BankService bankService;

    @GetMapping("/new")
    public ResponseEntity<BankModel> newBank(@RequestBody BankModel bank) {
        return ResponseEntity.ok(bankService.createBank(bank));
    }
    @PostMapping("/agency/add")
    public ResponseEntity<Object> addAgency(@RequestBody AgencyCreateRequest agency) {
        return ResponseEntity.ok(bankService.addAgency(agency));
    }

    @PostMapping("/agency/remove/{agencyId}")
    public void removeAgency(@RequestParam Long agencyId) {
        bankService.removeAgency(agencyId);
    }
    
    

}
