package com.m1fonda.service_notif.services;

import java.util.Date;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.m1fonda.service_notif.entities.Notification;

@Service
public class NotificationService {

    public Notification sendNotification(Map<String, String> notifInfo) {
        return Notification.builder()
                .createAt(new Date())
                .email(notifInfo.get("email"))
                .picture(notifInfo.get("picture"))
                .title(notifInfo.get("title"))
                .description(notifInfo.get("description"))
                .build();
    }
}
