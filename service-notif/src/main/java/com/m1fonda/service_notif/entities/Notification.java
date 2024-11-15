package com.m1fonda.service_notif.entities;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.m1fonda.entities.Announce;

import lombok.Builder;
import lombok.NoArgsConstructor;

@Document(collection = "notifications")
@NoArgsConstructor
public class Notification extends Announce {

    @Id
    private Long id;

    @Builder
    public Notification(String title, String description, Date createAt, String picture, String email) {
        super(title, description, createAt, picture, email);
    }
}
