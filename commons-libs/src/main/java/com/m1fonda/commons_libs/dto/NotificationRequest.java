package com.m1fonda.commons_libs.dto;

import java.util.Date;

public record NotificationRequest(
        String notificationId,
        String userEmail,
        String agencyNum,
        String type,
        String message,
        Date date) {

}
