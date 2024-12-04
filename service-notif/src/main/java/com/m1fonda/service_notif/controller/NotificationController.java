package com.m1fonda.service_notif.controller;

import org.springframework.web.bind.annotation.RestController;

import com.m1fonda.service_notif.services.MessageNotificationService;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/api/notifications")
@RestController
@AllArgsConstructor
public class NotificationController {

    private final MessageNotificationService service;

    @PostMapping("/delete/{id}")
    public void deleteNotification(@RequestParam String id) {
        service.deleteNotification(id);
    }
    
    @PostMapping("/delete/agency/{id}")
    public void deleteAgencyNotification(@RequestParam String id) {
        service.deleteAgencyNotification(id);
    }
    
    @PostMapping("/delete/user/{email}")
    public void deleteUserNotification(@RequestParam String email) {
        service.deleteUserNotification(email);
    }
    

}
