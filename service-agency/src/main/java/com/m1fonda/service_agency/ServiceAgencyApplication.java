package com.m1fonda.service_agency;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class ServiceAgencyApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceAgencyApplication.class, args);
	}

}
