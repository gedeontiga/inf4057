package com.m1fonda.service_bank.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@Document(collection = "agencies")
@AllArgsConstructor
@Builder
public class AgencyModel  {

    @Id
    private String id;

    private String numBank;

    @Indexed(unique = true)
    @Field("num_Agency")
    private String numAgency;

    private String name;
    private double capital;
    private String address;
    
}
