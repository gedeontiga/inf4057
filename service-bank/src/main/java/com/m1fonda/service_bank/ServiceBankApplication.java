package com.m1fonda.service_bank;

import java.util.Map;
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

	final String DASH = "-";

	public static void main(String[] args) {
		SpringApplication.run(ServiceBankApplication.class, args);
	}

	@Bean
	CommandLineRunner start(BankRepository bankRepository, AgencyRepository agencyRepository) throws RuntimeException {

		return args -> {
			Random rand = new Random();
			Map.of("CCA",
					"https://lh3.googleusercontent.com/tpvg8GK9k3m3Trur8ra1IGcu1ZqqJ_Ph1nKTuCoafCj-7z6po5gmNmzT8SxrbZP7rf8",
					"UBA", "https://www.diasporaction.fr/wp-content/uploads/2017/01/UBA-logo.jpg").entrySet()
					.forEach(bank -> {
						if (bankRepository.findByName(bank.getKey()).isEmpty()) {
							String numBank = bank.getKey() + DASH + bank.getKey().hashCode();
							BankModel banque = BankModel.builder()
									.name(bank.getKey())
									.bankNumber(numBank)
									.capital(1000000000000000.0)
									.contact("6" + rand.nextLong(99999999))
									.logo(bank.getValue())
									.type("GLOBAL")
									.ownerEmail("admin." + bank.getKey().toLowerCase() + "@atg-bank.com")
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
			String numAgency = bank.getBankNumber() + DASH + address + DASH + address.hashCode();
			if (agencyRepository.findByAddressAndNumAgency(address, numAgency).isEmpty())
				bank.addAgency(
						agencyRepository.save(
								AgencyModel.builder()
										.capital(1000000000)
										.numAgency(numAgency)
										.address(address)
										.name(bank.getName() + " " + address)
										.numBank(bank.getBankNumber())
										.build()));
		});
	}

}
