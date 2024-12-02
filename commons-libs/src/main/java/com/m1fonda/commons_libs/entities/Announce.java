package com.m1fonda.commons_libs.entities;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Announce implements Serializable {
    private String header;
    private String userId1;
    private String userId2;
    private String agencyId;
    private String message;
    private final Date createdAt = new Date();
}
