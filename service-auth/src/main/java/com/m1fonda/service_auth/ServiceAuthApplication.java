package com.m1fonda.service_auth;

import java.io.IOException;
import java.util.stream.Stream;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

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
	CommandLineRunner start(UserRepository userRepository, RoleRepository roleRepository,
			PasswordEncoder passwordEncoder) throws Exception {
		// Step 1: Ensure roles are present
		for (RoleType roleType : RoleType.values()) {
			roleRepository.save(roleRepository.findByType(roleType).orElse(new Role(roleType)));
		}

		return args -> {
			Stream<String> adminStream = Stream.of("admin1", "admin2");
			defaultSaveUser(adminStream, userRepository, roleRepository.findByType(RoleType.ADMIN).orElseThrow(),
					passwordEncoder);
			Stream<String> ownerStream = Stream.of("owner.uba", "owner.cca");
			defaultSaveUser(ownerStream, userRepository, roleRepository.findByType(RoleType.OWNER).orElseThrow(),
					passwordEncoder);
			Stream<String> managerStream = Stream.of("manager1.uba", "manager2.uba", "manager3.uba", "manager1.cca",
					"manager2.cca", "manager3.cca");
			defaultSaveUser(managerStream, userRepository, roleRepository.findByType(RoleType.MANAGER).orElseThrow(),
					passwordEncoder);
		};
	}

	public void defaultSaveUser(Stream<String> stream, UserRepository userRepository, Role role,
			PasswordEncoder passwordEncoder) throws IOException {
		final String bankDomainName = "@atg-bank.com";

		stream.forEach(username -> {
			userRepository.save(userRepository.findByEmail(username + bankDomainName)
					.orElse(
							Users.builder()
									.email(username + bankDomainName)
									.enabled(true)
									.password(passwordEncoder.encode(username + ".password"))
									.role(role)
									.build()));
		});
		userRepository.findAll().forEach(System.out::println);

	}
}
