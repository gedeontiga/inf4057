package com.m1fonda.service_withdrawal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import com.m1fonda.commons_libs.dto.AccountDepositWithdrawalResponse;
import com.m1fonda.commons_libs.dto.WithdrawalRequest;
import com.m1fonda.commons_libs.dto.WithdrawalResponse;
import com.m1fonda.commons_libs.dto.UserResponse;
import com.m1fonda.service_withdrawal.service.WithdrawalService;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
@AllArgsConstructor
@RequestMapping("/api/withdrawal")
public class WithdrawalController {

    private final WithdrawalService withdrawalService;

    @PostMapping("/")
    public ResponseEntity<WithdrawalResponse> withdrawal(@RequestBody WithdrawalRequest withdrawalRequest) {
        RestTemplate restTemplate = new RestTemplate();
        String userUrl = "http://localhost:8075/api/user/read";
        UserResponse user = restTemplate.getForObject(userUrl, UserResponse.class);

        if (user != null) {
            AccountDepositWithdrawalResponse request = new AccountDepositWithdrawalResponse(null, null, withdrawalRequest.accountNum(), user.firstName() + " " + user.lastName(), withdrawalRequest.agencyCode(), user.email(), withdrawalRequest.amount(), 0, 0, null);
            return ResponseEntity.ok(withdrawalService.newWithdrawal(request));
        }

        return ResponseEntity.badRequest().body(null);
    }
    

}
