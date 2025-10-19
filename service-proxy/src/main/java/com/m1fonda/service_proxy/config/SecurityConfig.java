package com.m1fonda.service_proxy.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import com.m1fonda.service_proxy.components.JwtAuthenticationWebFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private final JwtAuthenticationWebFilter jwtAuthenticationWebFilter;
	@Value("${cors.allowed-origins}")
	private String allowedOrigins;

	@Bean
	SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
		return http
				.csrf(ServerHttpSecurity.CsrfSpec::disable)
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
				.formLogin(ServerHttpSecurity.FormLoginSpec::disable)
				.logout(ServerHttpSecurity.LogoutSpec::disable)
				.authorizeExchange(exchanges -> exchanges
						.pathMatchers("/api/auth/**").permitAll()
						.pathMatchers("/api/demande/**").permitAll()
						.pathMatchers("/api/bank/**").permitAll()
						.anyExchange().authenticated())
				.exceptionHandling(exception -> exception
						.authenticationEntryPoint((exchange, ex) -> {
							exchange.getResponse().setStatusCode(
									org.springframework.http.HttpStatus.UNAUTHORIZED);
							return exchange.getResponse().setComplete();
						}))
				.securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
				.addFilterAt(jwtAuthenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
				.build();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		String[] origins = allowedOrigins.split(",");
		configuration.setAllowedOriginPatterns(Arrays.asList(origins));

		configuration.setAllowedMethods(Arrays.asList(
				"GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD"));

		configuration.setAllowedHeaders(List.of("*"));
		configuration.setAllowCredentials(true);
		configuration.setExposedHeaders(Arrays.asList(
				"Authorization", "Content-Type", "X-Total-Count"));
		configuration.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);

		return source;
	}
}