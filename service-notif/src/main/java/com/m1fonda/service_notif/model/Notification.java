package com.m1fonda.service_notif.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.*;

@Document( collection = "notifications")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notification {
    @Id
    private String id;

    @Indexed(unique = true)
    private String notificationId;
    private String type;
    private String userEmail;
    private String agencyNum;
    private String message;
    private final Date createdAt = new Date();
}
