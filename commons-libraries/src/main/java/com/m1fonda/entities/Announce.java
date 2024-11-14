package com.m1fonda.entities;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class Announce implements Serializable {
    private String titre;
    private String description;
    private Date dateEnvoi;
    private String pj;

    protected Announce() {
    }

    protected Announce(String titre, String description, Date dateEnvoi, String pj) {
        this.titre = titre;
        this.description = description;
        this.dateEnvoi = dateEnvoi;
        this.pj = pj;
    }
}
