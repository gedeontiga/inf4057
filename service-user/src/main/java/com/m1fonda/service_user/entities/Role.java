package com.m1fonda.service_user.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document(collection = "role")
public class Role {

    @Id
    private Long id;

    private RoleType type;

    public Role(RoleType type) {
        this.type = type;
    }
}
