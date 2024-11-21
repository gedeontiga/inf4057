package com.m1fonda.commons_libs.entities;

import java.io.Serializable;
import java.util.Date;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Announce implements Serializable {
    private String title;
    private String description;
    private Date createAt;
    private String picture;
    private String email;

    public Announce(String title, String description, Date createAt, String picture, String email) {
        this.title = title;
        this.description = description;
        this.createAt = createAt;
        this.picture = picture;
        this.email = email;
    }

    public Announce() {
    }
}
