package com.m1fonda.service_agency;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

import com.m1fonda.service_agency.entities.Agence;
import com.m1fonda.service_agency.entities.Managers;
import com.m1fonda.service_agency.repositories.AgencyRepository;
import com.m1fonda.service_agency.repositories.ManagersRepository;

@EnableDiscoveryClient
@SpringBootApplication
public class ServiceAgencyApplication {

	final String DASH = "-";

	public static void main(String[] args) {
		SpringApplication.run(ServiceAgencyApplication.class, args);
	}

	@Bean
	CommandLineRunner start(AgencyRepository agencyRepository, ManagersRepository managersRepository)
			throws RuntimeException {

		return args -> {
			final String bankDomainName = "@atg-bank.com";
			Stream.of("CCA", "UBA").forEach(bank -> {
				String numBank = bank + DASH + bank.hashCode();
				AtomicInteger index = new AtomicInteger(0);
				Stream.of("Bastos", "Omnisports", "Poste").forEach(address -> {
					Integer addressCode = address.hashCode() > 0 ? address.hashCode() : -1 * address.hashCode();
					String numAgency = numBank + DASH + address + DASH + addressCode.toString();
					String emailManager = "manager" + index.incrementAndGet() + "." + bank.toLowerCase()
							+ bankDomainName;
					String emailOwner = "owner." + bank.toLowerCase() + bankDomainName;
					if (!managersRepository.findByEmail(emailManager).isPresent())
						managersRepository.save(Managers.builder()
								.email(emailManager)
								.numAgency(numAgency)
								.numCni((bank.hashCode() + address.hashCode() + emailManager.hashCode()) + "").build());
					if (!managersRepository.findByEmail(emailOwner).isPresent())
						managersRepository.save(Managers.builder()
								.email(emailOwner)
								.numAgency(numAgency)
								.numCni((bank.hashCode() + address.hashCode() + emailOwner.hashCode()) + "").build());

					if (agencyRepository.findByAddressAndNumAgency(address, numAgency).isEmpty()) {
						agencyRepository.save(
								Agence.builder()
										.capital(1000000000)
										.numAgency(numAgency)
										.address(address)
										.name(bank + " " + address)
										.numBank(numBank)
										.build());
					}
				});
			});
		};
	}
}
