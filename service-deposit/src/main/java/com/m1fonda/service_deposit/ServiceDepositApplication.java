package com.m1fonda.service_deposit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class ServiceDepositApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceDepositApplication.class, args);
	}

}
