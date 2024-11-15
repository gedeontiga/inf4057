package com.m1fonda.service_transfer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@EnableDiscoveryClient
@SpringBootApplication
public class ServiceTransferApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceTransferApplication.class, args);
	}

}
