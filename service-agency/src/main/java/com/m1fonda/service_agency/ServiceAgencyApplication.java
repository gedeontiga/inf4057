package com.m1fonda.service_agency;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
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

	final String DASH = "-";

	public static void main(String[] args) {
		SpringApplication.run(ServiceAgencyApplication.class, args);
	}

	@Bean
	CommandLineRunner start(AgencyRepository agencyRepository) throws RuntimeException {

		return args -> {
			final String bankDomainName = "@atg-bank.com";
			Stream.of("CCA", "UBA").forEach(bank -> {
				String numBank = bank + DASH + bank.hashCode();
				AtomicInteger index = new AtomicInteger(0);
				Stream.of("Bastos", "Omnisports", "Poste").forEach(address -> {
					String numAgency = numBank + DASH + address + DASH + address.hashCode();
					if (agencyRepository.findByAddressAndNumAgency(address, numAgency).isEmpty()) {
						agencyRepository.save(
								Agence.builder()
										.capital(1000000000)
										.numAgency(numAgency)
										.address(address)
										.name(bank + " " + address)
										.numBank(numBank)
										.agents(Set.of("manager" + index.incrementAndGet() + bankDomainName,
												"admin." + bank.toLowerCase() + bankDomainName))
										.build());
					}
				});
			});
		};
	}
}
