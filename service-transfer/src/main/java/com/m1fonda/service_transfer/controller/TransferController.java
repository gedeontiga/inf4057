package com.m1fonda.service_transfer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import com.m1fonda.commons_libs.dto.TransferResponse;
import com.m1fonda.commons_libs.dto.TransferRequest;
import com.m1fonda.commons_libs.dto.UserResponse;
import com.m1fonda.service_transfer.service.TransferService;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
@AllArgsConstructor
@RequestMapping("/api/transfer")
public class TransferController {

    private final TransferService transferService;

    @PostMapping("/")
    public ResponseEntity<TransferResponse> transfer(@RequestBody TransferRequest transferRequest) {
        RestTemplate restTemplate = new RestTemplate();
        String userUrl1 = "http://localhost:8075/api/user/read" ;
        String userUrl2 = "http://localhost:8075/api/user/read/"+transferRequest.receiverEmail() ;

        UserResponse sender = restTemplate.getForObject(userUrl1, UserResponse.class);
        UserResponse receiver = restTemplate.getForObject(userUrl2, UserResponse.class);

        if (sender != null && receiver != null) {
        TransferRequest response = new TransferRequest(
                                        transferRequest.senderAccountNum(), 
                                        transferRequest.senderAgency(), 
                                        transferRequest.receiverAccountNum(), 
                                        transferRequest.receiverAgency(), 
                                        null,
                                        sender.email(), 
                                        receiver.email(), 
                                        sender.firstName() + " " + sender.lastName(), 
                                        receiver.firstName() + " " + receiver.lastName(), 
                                        null, 
                                        transferRequest.amount(), 
                                        0, 
                                        0,
                                        0,
                                        null);
            return ResponseEntity.ok(transferService.newTransfer(response));
        }

        return ResponseEntity.badRequest().body(null);
    }
    

}
