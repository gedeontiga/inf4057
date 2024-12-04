package com.m1fonda.service_account.controllers;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.m1fonda.commons_libs.dto.AccountDTO;
import com.m1fonda.commons_libs.dto.AccountRequestTransferDTO;
import com.m1fonda.commons_libs.dto.AccountResponseTransferDTO;
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
        return accountService.getAccount(user.getUsername());
    }

    @PostMapping("/info-transfer")
    public AccountResponseTransferDTO getTransferInfo(@RequestBody AccountRequestTransferDTO request) {
        return accountService.getTransferInfos(request);
    }

}
