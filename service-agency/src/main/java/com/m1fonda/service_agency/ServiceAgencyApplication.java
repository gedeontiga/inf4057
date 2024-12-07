package com.m1fonda.service_agency;

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
			Stream.of("CCA", "UBA").forEach(bank -> {
				Stream.of("Bastos", "Omnisports", "Poste").forEach(address -> {
					String numAgency = bank + "-" + address + "-001";
					if (agencyRepository.findByAddressAndNumAgency(address, numAgency).isEmpty())
						agencyRepository.save(
								Agence.builder()
										.capital(1000000000)
										.numAgency(numAgency)
										.address(address)
										.name("ATG Agence")
										.numBank(bank + "-001")
										.build());
				});
			});
		};
	}
}
