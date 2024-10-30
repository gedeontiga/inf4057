package com.m1fonda.service_user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class ServiceUserApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceUserApplication.class, args);
	}

	// @Bean
	// CommandLineRunner start(UserRepository userRepository) {
	// Random random = new Random();
	// return args -> {
	// Stream.of("Test", "Try", "Catch").forEach(username -> {
	// String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
	// StringBuilder result = new StringBuilder(9);
	// new Random().ints(9, 0, characters.length()).forEach(i ->
	// result.append(characters.charAt(i)));
	// userRepository.save(new Users(null, username, result.toString(), username +
	// "@example.com",
	// random.nextLong(999999999)));
	// });
	// userRepository.findAll().forEach(System.out::println);
	// };
	// }
}
