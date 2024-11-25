package com.m1fonda.service_account.controllers;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.m1fonda.commons_libs.dto.AccountDTO;
import com.m1fonda.service_account.services.CompteService;

import lombok.AllArgsConstructor;

@RequestMapping(path = "/api/account")
@AllArgsConstructor
public class CompteController {

    private final CompteService accountService;

    @GetMapping(path = "/get-account")
    public AccountDTO getAccount() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return accountService.getAccount(user.getUsername());
    }
}
