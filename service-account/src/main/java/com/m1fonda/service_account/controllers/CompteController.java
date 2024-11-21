package com.m1fonda.service_account.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.m1fonda.commons_libs.dto.AccountDTO;
import com.m1fonda.service_account.services.CompteService;

import lombok.AllArgsConstructor;

@RequestMapping(path = "/api/account")
@AllArgsConstructor
public class CompteController {

    private final CompteService accountService;

    @PostMapping(path = "/get-account")
    public AccountDTO getAccount(@RequestBody String numAccount) {
        return accountService.getAccount(numAccount);
    }
}
