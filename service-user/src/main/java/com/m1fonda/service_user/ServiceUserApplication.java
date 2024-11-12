package com.m1fonda.service_user;

import java.util.Random;
import java.util.stream.Stream;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

import com.m1fonda.service_user.entities.Users;
import com.m1fonda.service_user.repositories.UserRepository;

@EnableDiscoveryClient
@SpringBootApplication
public class ServiceUserApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceUserApplication.class, args);
	}

	@Bean
	CommandLineRunner start(UserRepository userRepository) {
		Random random = new Random();
		return args -> {
			Stream.of("Test", "Try", "Catch").forEach(username -> {
				String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
				StringBuilder result = new StringBuilder(9);
				new Random().ints(9, 0, characters.length()).forEach(i -> result.append(characters.charAt(i)));
				userRepository.save(
						Users.builder().cni(result.toString())
								.email(username + "@example.com")
								.nom(username)
								.prenom(username)
								.password("test...")
								.tel(random.nextLong(999999999))
								.build());
				userRepository.findAll().forEach(System.out::println);
			});
		};
	}
}
