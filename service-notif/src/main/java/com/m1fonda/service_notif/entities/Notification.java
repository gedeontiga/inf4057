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
    private Users users;

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    @Builder
    public Notification(String titre, String description, Date dateEnvoi, String pj, Users users) {
        super(titre, description, dateEnvoi, pj);
        this.users = users;
    }
}
