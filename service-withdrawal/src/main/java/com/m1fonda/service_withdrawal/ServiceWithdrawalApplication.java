package com.m1fonda.service_withdrawal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class ServiceWithdrawalApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceWithdrawalApplication.class, args);
	}

}
