package com.m1fonda.entities;

import java.io.Serializable;

import lombok.Data;

@Data
public class Announce implements Serializable {
    private String titre;
    private String description;
    private String datePublication;
    private String pj;

    protected Announce() {
    }

    protected Announce(String titre, String description, String datePublication, String pj) {
        this.titre = titre;
        this.description = description;
        this.datePublication = datePublication;
        this.pj = pj;
    }
}
