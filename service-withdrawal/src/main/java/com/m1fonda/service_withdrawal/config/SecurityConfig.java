package com.m1fonda.service_withdrawal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.m1fonda.service_withdrawal.components.GatewayAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final GatewayAuthenticationFilter gatewayAuthenticationFilter;

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
				.csrf(csrf -> csrf.disable())
				.cors(cors -> cors.disable())

				.authorizeHttpRequests(authorize -> authorize

						.requestMatchers("/actuator/health", "/actuator/info").permitAll()

						.requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()

						.anyRequest().authenticated())

				.sessionManagement(session -> session
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				.addFilterBefore(gatewayAuthenticationFilter,
						UsernamePasswordAuthenticationFilter.class)

				.build();
	}
}