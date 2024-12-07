package com.m1fonda.service_bank.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.m1fonda.service_bank.dto.BankDTOResponse;
import com.m1fonda.service_bank.dto.FeesDTO;
import com.m1fonda.service_bank.service.BankService;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@AllArgsConstructor
@RestController
@RequestMapping("/api/bank")
public class BankController {

    private final BankService bankService;

    @GetMapping("/all")
    public ResponseEntity<List<BankDTOResponse>> getAll() {
        return ResponseEntity.ok(bankService.getAll());
    }

    @GetMapping("/get/{param}")
    public ResponseEntity<BankDTOResponse> getBank(@PathVariable String param) {
        return ResponseEntity.ok(bankService.getBank(param));
    }

    @GetMapping("/fees/{agencyNum}")
    public ResponseEntity<FeesDTO> getMethodName(@PathVariable String agencyNum) {
        return ResponseEntity.ok(bankService.getFees(agencyNum));
    }
}
