package com.m1fonda.service_notif.dto;

import java.util.Date;

public record NotificationRequest(
    String notificationId,
    String userEmail,
    String agencyNum,
    String type,
    String message,
    Date date
    ) {

}

