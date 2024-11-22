package com.m1fonda.service_user;

import java.util.Random;
import java.util.stream.Stream;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

import com.m1fonda.service_user.entities.Role;
import com.m1fonda.service_user.entities.RoleType;
import com.m1fonda.service_user.entities.Users;
import com.m1fonda.service_user.repositories.RoleRepository;
import com.m1fonda.service_user.repositories.UserRepository;

@EnableDiscoveryClient
@SpringBootApplication
public class ServiceUserApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceUserApplication.class, args);
	}

	@Bean
	CommandLineRunner start(UserRepository userRepository, RoleRepository roleRepository) {
		return args -> {
			// Step 1: Ensure roles are present
			for (RoleType roleType : RoleType.values()) {
				if (!roleRepository.findByType(roleType).isPresent()) {
					roleRepository.save(new Role(roleType));
				}
			}

			// Step 2: Add users if none exist
			if (userRepository.count() == 0) {
				Stream.of("admin1", "admin2").forEach(username -> {
					if (!userRepository.findByEmail(username + "@atg-bank.com").isPresent()) {
						userRepository.save(
								Users.builder()
										.cni(java.util.UUID.randomUUID().toString()) // Unique CNI
										.email(username + "@atg-bank.com")
										.firstName(username)
										.lastName(username)
										.password(username + ".test")
										.phoneNumber(new Random().nextLong(699999999)) // Mocked phone number
										.role(roleRepository.findByType(RoleType.ADMIN).orElseThrow())
										.enabled(true) // Mark the user as enabled
										.build());
					}
				});

				Stream.of("manager1", "manager2").forEach(username -> {
					if (!userRepository.findByEmail(username + "@atg-bank.com").isPresent()) {
						userRepository.save(
								Users.builder()
										.cni(java.util.UUID.randomUUID().toString()) // Unique CNI
										.email(username + "@atg-bank.com")
										.firstName(username)
										.lastName(username)
										.password(username + ".test")
										.phoneNumber(new Random().nextLong(699999999)) // Mocked phone number
										.role(roleRepository.findByType(RoleType.MANAGER).orElseThrow())
										.enabled(true) // Mark the user as enabled
										.build());
					}
				});
			}

			// Step 3: Print all users to the console
			userRepository.findAll().forEach(System.out::println);
		};
	}

}
