package com.m1fonda.service_demands;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class ServiceDemandsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceDemandsApplication.class, args);
	}

}
