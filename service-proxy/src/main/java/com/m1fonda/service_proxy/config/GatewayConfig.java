package com.m1fonda.service_proxy.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class GatewayConfig {
	@Bean
	RouteLocator routes(RouteLocatorBuilder builder) {
		return builder.routes()
				.route("service-agency", r -> r
						.path("/api/demande/**")
						.uri("lb://service-agency"))
				.route("service-bank", r -> r
						.path("/api/bank/**")
						.uri("lb://service-bank"))
				.route("service-user", r -> r
						.path("/api/user/**", "/api/manager/**", "/api/owner/**",
								"/api/admin/**")
						.uri("lb://service-user"))
				.route("service-account", r -> r
						.path("/api/account/**")
						.uri("lb://service-account"))
				.route("service-agency", r -> r
						.path("/api/agency/**")
						.uri("lb://service-agency"))
				.route("service-deposit", r -> r
						.path("/api/deposit/**")
						.uri("lb://service-deposit"))
				.route("service-bank", r -> r
						.path("/api/owner-bank/**")
						.uri("lb://service-bank"))
				.route("service-withdrawal", r -> r
						.path("/api/withdrawal/**")
						.uri("lb://service-withdrawal"))
				.route("service-transfer", r -> r
						.path("/api/transfer/**")
						.uri("lb://service-transfer"))
				.build();
	}

	@Bean
	CorsWebFilter corsWebFilter() {
		CorsConfiguration config = new CorsConfiguration();
		config.addAllowedOrigin("*"); // Toutes les origines autorisées
		config.addAllowedMethod("*"); // Toutes les méthodes autorisées
		config.addAllowedHeader("*"); // Tous les headers autorisés

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return new CorsWebFilter(source);
	}
}