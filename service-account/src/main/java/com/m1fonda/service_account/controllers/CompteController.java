package com.m1fonda.service_account.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.m1fonda.commons_libs.dto.AccountDTO;
import com.m1fonda.commons_libs.dto.AccountRequestTransferDTO;
import com.m1fonda.commons_libs.dto.AccountResponseTransferDTO;
import com.m1fonda.service_account.dto.AccountUpdateDTO;
import com.m1fonda.service_account.services.CompteService;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@AllArgsConstructor
@RequestMapping("/api/account")
public class CompteController {

    private final CompteService accountService;

    @GetMapping("/get-account")
    public List<AccountDTO> getAccount() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return accountService.getAccounts(user.getUsername());
    }

    @GetMapping("/get-account/{numAccount}")
    public AccountDTO getMethodName(@PathVariable String numAccount) {
        return accountService.getAccountByAccountId(numAccount);
    }

    @PreAuthorize("hasRole('MANAGER', 'OWNER')")
    @GetMapping("/count-client/agency/{numAgency}")
    public ResponseEntity<Long> getClentNumberByAgency(@PathVariable String numAgency) {
        return ResponseEntity.ok(accountService.countClientByAgency(numAgency));
    }

    @PreAuthorize("hasRole('MANAGER', 'OWNER')")
    @PostMapping("/update")
    public ResponseEntity<AccountUpdateDTO> updateAccount(@RequestBody AccountUpdateDTO account) {
        return ResponseEntity.ok(accountService.updateAccount(account));
    }

    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/count-client/bank/{numBank}")
    public ResponseEntity<Long> getClientNumberByBank(@PathVariable String numBank) {
        return ResponseEntity.ok(accountService.countClientByBank(numBank));
    }

    @PostMapping("/info-transfer")
    public AccountResponseTransferDTO getTransferInfo(@RequestBody AccountRequestTransferDTO request) {
        return accountService.getTransferInfos(request);
    }

}
