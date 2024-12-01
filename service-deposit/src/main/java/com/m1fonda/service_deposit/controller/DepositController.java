package com.m1fonda.service_deposit.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import com.m1fonda.commons_libs.dto.AccountDepositWithdrawalResponse;
import com.m1fonda.commons_libs.dto.DepositRequest;
import com.m1fonda.commons_libs.dto.DepositResponse;
import com.m1fonda.commons_libs.dto.UserResponse;
import com.m1fonda.service_deposit.service.DepositService;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
@AllArgsConstructor
@RequestMapping("/api/deposit")
public class DepositController {

    private final DepositService depositService;

    @PostMapping("/")
    public ResponseEntity<DepositResponse> deposit(@RequestBody DepositRequest depositRequest) {
        RestTemplate restTemplate = new RestTemplate();
        String userUrl = "http://localhost:8075/api/user/read" ;
        UserResponse user = restTemplate.getForObject(userUrl, UserResponse.class);

        if (user != null) {
            AccountDepositWithdrawalResponse request = new AccountDepositWithdrawalResponse(null, null, depositRequest.accountNum(), user.firstName() + " " + user.lastName(), depositRequest.agencyCode(), user.email(), depositRequest.amount(), 0, 0, null);
            return ResponseEntity.ok(depositService.newDeposit(request));
        }

        return ResponseEntity.badRequest().body(null);
    }
    

}
