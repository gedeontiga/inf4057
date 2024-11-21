package com.m1fonda.service_auth;

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
	CommandLineRunner start(UserRepository userRepository, RoleRepository roleRepository) throws RuntimeException {
		Random random = new Random();
		Set<Role> roles = Set.of(new Role(Long.valueOf(1), RoleType.USER),
				new Role(Long.valueOf(2), RoleType.MANAGER),
				new Role(Long.valueOf(3), RoleType.ADMIN));

		roleRepository.saveAll(roles);

		return args -> {
			Stream.of("admin1", "admin2").forEach(username -> {
				String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
				StringBuilder result = new StringBuilder(11);
				new Random().ints(11, 0, characters.length()).forEach(i -> result.append(characters.charAt(i)));
				userRepository.save(
						Users.builder().cni(result.toString())
								.email(username + "@atg-bank.com")
								.firstName(username)
								.lastName(username)
								.password(username + ".test")
								.phoneNumber(random.nextLong(699999999))
								.role(roleRepository.findByType(RoleType.ADMIN))
								.build());
				userRepository.findAll().forEach(System.out::println);
			});
			Stream.of("manager1", "manager2").forEach(username -> {
				String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
				StringBuilder result = new StringBuilder(11);
				new Random().ints(11, 0, characters.length()).forEach(i -> result.append(characters.charAt(i)));
				userRepository.save(
						Users.builder().cni(result.toString())
								.email(username + "@atg-bank.com")
								.firstName(username)
								.lastName(username)
								.password(username + ".test")
								.phoneNumber(random.nextLong(699999999))
								.role(roleRepository.findByType(RoleType.MANAGER))
								.build());
				userRepository.findAll().forEach(System.out::println);
			});
		};
	}
}
