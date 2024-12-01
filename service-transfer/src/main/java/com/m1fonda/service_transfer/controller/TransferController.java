package com.m1fonda.service_transfer.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.m1fonda.commons_libs.dto.TransferResponse;
import com.m1fonda.commons_libs.dto.TransferRequestDTO;
import com.m1fonda.service_transfer.dto.TransferFilterDTO;
import com.m1fonda.service_transfer.model.Transfer;
import com.m1fonda.service_transfer.service.TransferService;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
@AllArgsConstructor
@RequestMapping("/api/transfer")
public class TransferController {

    private final TransferService transferService;

    @PostMapping("/")
    public ResponseEntity<TransferResponse> transfer(@RequestBody TransferRequestDTO transferRequest) {
        return ResponseEntity.ok(transferService.newTransfer(transferRequest));
    }
    
    @GetMapping("/filter")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Transfer>> filterTransfers(
        @RequestParam(required = false) String email,
        @RequestParam(required = false) String accountId,
        @RequestParam(required = false) String agencyId,
        @RequestParam(required = false) String bankId
    ) {
        TransferFilterDTO filterDTO = new TransferFilterDTO();
        filterDTO.setEmail(email);
        filterDTO.setAccountId(accountId);
        filterDTO.setAgencyId(agencyId);
        filterDTO.setBankId(bankId);

        List<Transfer> transfers = transferService.filterTransfers(filterDTO);
        return ResponseEntity.ok(transfers);
    }

}
