package com.m1fonda.service_transfer.controller;


import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.m1fonda.service_transfer.dto.TransferRequest;
import com.m1fonda.service_transfer.dto.TransferResponse;
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
    public ResponseEntity<TransferResponse> transfer(@RequestBody TransferRequest transferRequest) {
        return ResponseEntity.ok(transferService.newTransfer(transferRequest));
    }
    
    @GetMapping("/readall")
    public ResponseEntity<List<TransferResponse>> filterTransfers(
        @RequestParam(required = false) String accountId,
        @RequestParam(required = false) String agencyId
    ) {

        List<TransferResponse> transfers = transferService.filterTransfers(accountId, agencyId);
        return ResponseEntity.ok(transfers);
    }

}
