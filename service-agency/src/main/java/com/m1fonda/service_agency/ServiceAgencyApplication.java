package com.m1fonda.service_agency;

import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

import com.m1fonda.service_agency.entities.Agence;
import com.m1fonda.service_agency.repositories.AgencyRepository;

@EnableDiscoveryClient
@SpringBootApplication
public class ServiceAgencyApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceAgencyApplication.class, args);
	}

	@Bean
	CommandLineRunner start(AgencyRepository agencyRepository) throws RuntimeException {

		return args -> {
			Stream.of("Bastos", "Santa Barbara").forEach(address -> {
				String uuid = UUID.randomUUID().toString().replace("-", "");
				String numAgency = uuid.substring(0, 8);
				if (agencyRepository.count() < 3)
					agencyRepository.save(
							Agence.builder()
									.capital(1000000000)
									.numAgency("ATG-" + numAgency)
									.address(address)
									.name("ATG Agence")
									.numBank("ATG-001")
									.build());
			});
		};
	}
}
