package com.m1fonda.service_deposit.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
// import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.m1fonda.service_deposit.dto.DepositFilterDTO;
import com.m1fonda.service_deposit.model.Deposit;

// import com.m1fonda.commons_libs.dto.DepositRequest;
// import com.m1fonda.commons_libs.dto.DepositResponse;
import com.m1fonda.service_deposit.service.DepositService;

import lombok.AllArgsConstructor;

// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;


@Controller
@AllArgsConstructor
@RequestMapping("/api/deposit")
public class DepositController {

    private final DepositService depositService;

    @GetMapping("/filter")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Deposit>> filterDeposits(
        @RequestParam(required = false) String email,
        @RequestParam(required = false) String accountId,
        @RequestParam(required = false) String agencyId,
        @RequestParam(required = false) String bankId
    ) {
        DepositFilterDTO filterDTO = new DepositFilterDTO();
        filterDTO.setEmail(email);
        filterDTO.setAccountId(accountId);
        filterDTO.setAgencyId(agencyId);
        filterDTO.setBankId(bankId);

        List<Deposit> deposits = depositService.filterDeposits(filterDTO);
        return ResponseEntity.ok(deposits);
    }
    // private final DepositService depositService;

    // @PostMapping("/")
    // public ResponseEntity<DepositResponse> deposit(@RequestBody DepositRequest depositRequest) {
    //     return ResponseEntity.ok(depositService.newDeposit(depositRequest));
    // }

}
