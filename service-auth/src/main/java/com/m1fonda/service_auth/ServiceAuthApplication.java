package com.m1fonda.service_auth;

import java.io.IOException;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.m1fonda.service_auth.entities.Role;
import com.m1fonda.service_auth.entities.RoleType;
import com.m1fonda.service_auth.entities.Users;
import com.m1fonda.service_auth.repositories.RoleRepository;
import com.m1fonda.service_auth.repositories.UserRepository;

@SpringBootApplication
public class ServiceAuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceAuthApplication.class, args);
	}

	@Bean
	CommandLineRunner start(UserRepository userRepository, RoleRepository roleRepository) throws Exception {
		Set<Role> roles = Set.of(new Role(RoleType.USER),
				new Role(RoleType.MANAGER),
				new Role(RoleType.ADMIN));

		roles.forEach(role -> roleRepository.findByType(role.getType()).orElse(roleRepository.save(role)));

		return args -> {
			Stream<String> adminStream = Stream.of("admin1", "admin2");
			Stream<String> managerStream = Stream.of("manager1", "manager2");
			defaultSaveUser(adminStream, userRepository, roleRepository.findByType(RoleType.ADMIN).orElseThrow());
			defaultSaveUser(managerStream, userRepository, roleRepository.findByType(RoleType.MANAGER).orElseThrow());
		};
	}

	public void defaultSaveUser(Stream<String> stream, UserRepository userRepository, Role role) throws IOException {
		Random random = new Random();
		final String bankDomainName = "@atg-bank.com";

		stream.forEach(username -> {
			String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
			StringBuilder result = new StringBuilder(11);
			new Random().ints(11, 0, characters.length()).forEach(i -> result.append(characters.charAt(i)));
			userRepository.findByEmail(username + bankDomainName).orElse(
					userRepository.save(
							Users.builder().cni(result.toString())
									.email(username + bankDomainName)
									.firstName(username)
									.lastName(username)
									.password(username + ".test")
									.phoneNumber(random.nextLong(699999999))
									.role(role)
									.build()));
		});
		userRepository.findAll().forEach(System.out::println);

	}
}
