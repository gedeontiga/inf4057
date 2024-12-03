package com.m1fonda.service_withdrawal.controller;


import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.m1fonda.service_withdrawal.dto.WithdrawalRequest;
import com.m1fonda.service_withdrawal.dto.WithdrawalResponse;
import com.m1fonda.service_withdrawal.service.WithdrawalService;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
@AllArgsConstructor
@RequestMapping("/api/withdrawal")
public class WithdrawalController {

    private final WithdrawalService withdrawalService;

    @PostMapping("/")
    public ResponseEntity<WithdrawalResponse> withdrawal(@RequestBody WithdrawalRequest withdrawalRequest) {
        return ResponseEntity.ok(withdrawalService.newWithdrawal(withdrawalRequest));
    }

    @GetMapping("/readall")
    public ResponseEntity<List<WithdrawalResponse>> filterDeposits(
        @RequestParam(required = false) String accountId,
        @RequestParam(required = false) String agencyId
    ) {
        if (accountId != null || agencyId != null) {
            List<WithdrawalResponse> withdrawals = withdrawalService.filterWithdrawals(accountId, agencyId);
            return ResponseEntity.ok(withdrawals);
        }
        return ResponseEntity.badRequest().body(null);
    }

}
