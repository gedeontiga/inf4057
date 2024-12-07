package com.m1fonda.service_user;

import java.io.IOException;
import java.util.Random;
import java.util.stream.Stream;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

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
	CommandLineRunner start(UserRepository userRepository, RoleRepository roleRepository,
			PasswordEncoder passwordEncoder) throws Exception {
		// Step 1: Ensure roles are present
		for (RoleType roleType : RoleType.values()) {
			roleRepository.save(roleRepository.findByType(roleType).orElse(new Role(roleType)));
		}

		return args -> {
			Stream<String> adminStream = Stream.of("admin.uba", "admin.cca");
			defaultSaveUser(adminStream, userRepository, roleRepository.findByType(RoleType.ADMIN).orElseThrow(),
					passwordEncoder);
			Stream<String> managerStream = Stream.of("manager1", "manager2", "manager3");
			defaultSaveUser(managerStream, userRepository, roleRepository.findByType(RoleType.MANAGER).orElseThrow(),
					passwordEncoder);
		};
	}

	public void defaultSaveUser(Stream<String> stream, UserRepository userRepository, Role role,
			PasswordEncoder passwordEncoder) throws IOException {
		Random random = new Random();
		final String bankDomainName = "@atg-bank.com";

		stream.forEach(username -> {
			String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
			StringBuilder result = new StringBuilder(11);
			new Random().ints(11, 0, characters.length()).forEach(i -> result.append(characters.charAt(i)));
			userRepository.save(userRepository.findByEmail(username + bankDomainName)
					.orElse(
							Users.builder().cni(result.toString())
									.email(username + bankDomainName)
									.firstName(username)
									.lastName(username)
									.phoneNumber(Long.parseLong("600000000") + random.nextLong(99999999))
									.role(role)
									.build()));
		});
		userRepository.findAll().forEach(System.out::println);

	}

}
