package com.m1fonda.service_agency.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "managers")
public class Managers {

    @Id
    private String id;
    @Indexed(unique = true)
    private String numCni;
    @Indexed(unique = true)
    private String email;
    private String numAgency;

}
