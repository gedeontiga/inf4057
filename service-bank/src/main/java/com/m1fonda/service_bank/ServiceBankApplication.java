package com.m1fonda.service_bank;

import java.util.Random;
import java.util.stream.Stream;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

import com.m1fonda.service_bank.model.AgencyModel;
import com.m1fonda.service_bank.model.BankModel;
import com.m1fonda.service_bank.repository.AgencyRepository;
import com.m1fonda.service_bank.repository.BankRepository;

@EnableDiscoveryClient
@SpringBootApplication
public class ServiceBankApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceBankApplication.class, args);
	}

	@Bean
	CommandLineRunner start(BankRepository bankRepository, AgencyRepository agencyRepository) throws RuntimeException {

		return args -> {
			Random rand = new Random();
			Stream.of("CCA", "UBA").forEach(bank -> {
				if (bankRepository.findByName(bank).isEmpty()) {
					BankModel banque = BankModel.builder()
							.name(bank)
							.bankNumber(bank + "-001")
							.capital(1000000000000000.0)
							.contact("6" + rand.nextInt(8))
							.logo("assets/logo/" + bank.toLowerCase())
							.type("GLOBAL")
							.ownerEmail("admin." + bank.toLowerCase() + "@atg-bank.com")
							.externalFee(0.1)
							.transferFee(0.02)
							.withdrawFee(0.05)
							.build();
					createAgencies(banque, agencyRepository);
					bankRepository.save(banque);
				}
			});
		};
	}

	public void createAgencies(BankModel bank, AgencyRepository agencyRepository) throws RuntimeException {
		Stream.of("Bastos", "Omnisports", "Poste").forEach(address -> {
			String numAgency = bank.getName() + "-" + address + "-001";
			if (agencyRepository.findByAddressAndNumAgency(address, numAgency).isEmpty())
				bank.addAgency(
						agencyRepository.save(
								AgencyModel.builder()
										.capital(1000000000)
										.numAgency(numAgency)
										.address(address)
										.name("ATG Agence")
										.numBank(bank.getName() + "-001")
										.build()));
		});

	}

}
