package com.m1fonda.service_deposit.controller;


import java.util.List;

import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.m1fonda.service_deposit.dto.DepositRequest;
import com.m1fonda.service_deposit.dto.DepositResponse;

import com.m1fonda.service_deposit.service.DepositService;

import lombok.AllArgsConstructor;



@Controller
@AllArgsConstructor
@RequestMapping("/api/deposit")
public class DepositController {

    private final DepositService depositService;

    @GetMapping("/readall")
    public ResponseEntity<List<DepositResponse>> filterDeposits(
        @RequestParam(required = false) String accountId,
        @RequestParam(required = false) String agencyId
    ) {
        if (accountId != null || agencyId != null) {
            List<DepositResponse> deposits = depositService.filterDeposits(accountId, agencyId);
            return ResponseEntity.ok(deposits);
        }
        return ResponseEntity.ok(depositService.getAll());
    }

    @PostMapping("/")
    public ResponseEntity<DepositResponse> deposit(@RequestBody DepositRequest depositRequest) {
        return ResponseEntity.ok(depositService.newDeposit(depositRequest));
    }
    @PostMapping("/deleteall")
    public ResponseEntity<Object> remove() {
        depositService.deleteAll();
        return ResponseEntity.ok(null);
    }
 
}
