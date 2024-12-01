package com.m1fonda.service_withdrawal.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.m1fonda.commons_libs.dto.WithdrawalRequest;
import com.m1fonda.commons_libs.dto.WithdrawalResponse;
import com.m1fonda.service_withdrawal.dto.WithdrawalFilterDTO;
import com.m1fonda.service_withdrawal.model.Withdrawal;
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
    
    @GetMapping("/filter")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Withdrawal>> filterWithdrawals(
        @RequestParam(required = false) String email,
        @RequestParam(required = false) String accountId,
        @RequestParam(required = false) String agencyId,
        @RequestParam(required = false) String bankId
    ) {
        WithdrawalFilterDTO filterDTO = new WithdrawalFilterDTO();
        filterDTO.setEmail(email);
        filterDTO.setAccountId(accountId);
        filterDTO.setAgencyId(agencyId);
        filterDTO.setBankId(bankId);

        List<Withdrawal> withdrawals = withdrawalService.filterWithdrawals(filterDTO);
        return ResponseEntity.ok(withdrawals);
    }

}
