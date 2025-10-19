package com.m1fonda.service_proxy.config;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.codec.CodecCustomizer;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;

@Configuration
public class GatewayConfig {
	@Value("${gateway.secret}")
	private String gatewaySecret;

	@Bean
	RouteLocator routes(RouteLocatorBuilder builder) {
		return builder.routes()
				.route("service-bank", r -> r
						.path("/api/bank/**")
						.filters(f -> f.filter(authenticationHeaderFilter()))
						.uri("lb://service-bank"))
				.route("service-user", r -> r
						.path("/api/user/**", "/api/manager/**", "/api/owner/**",
								"/api/admin/**")
						.filters(f -> f.filter(authenticationHeaderFilter()))
						.uri("lb://service-user"))
				.route("service-account", r -> r
						.path("/api/account/**")
						.filters(f -> f.filter(authenticationHeaderFilter()))
						.uri("lb://service-account"))
				.route("service-agency", r -> r
						.path("/api/agency/**")
						.filters(f -> f.filter(authenticationHeaderFilter()))
						.uri("lb://service-agency"))
				.route("service-deposit", r -> r
						.path("/api/deposit/**")
						.filters(f -> f.filter(authenticationHeaderFilter()))
						.uri("lb://service-deposit"))
				.route("service-withdrawal", r -> r
						.path("/api/withdrawal/**")
						.filters(f -> f.filter(authenticationHeaderFilter()))
						.uri("lb://service-withdrawal"))
				.route("service-transfer", r -> r
						.path("/api/transfer/**")
						.filters(f -> f.filter(authenticationHeaderFilter()))
						.uri("lb://service-transfer"))
				.build();
	}

	private GatewayFilter authenticationHeaderFilter() {
		return (exchange, chain) -> ReactiveSecurityContextHolder.getContext()
				.flatMap(securityContext -> {
					Authentication auth = securityContext.getAuthentication();

					if (auth != null && auth.isAuthenticated()) {
						String email = auth.getName();
						String roles = auth.getAuthorities().stream()
								.map(authority -> authority.getAuthority().replace("ROLE_", ""))
								.collect(Collectors.joining(","));

						ServerWebExchange mutatedExchange = exchange.mutate()
								.request(builder -> builder
										.header("X-Gateway-Secret", gatewaySecret)
										.header("X-User-Email", email)
										.header("X-User-Roles", roles))
								.build();

						return chain.filter(mutatedExchange);
					}

					return chain.filter(exchange);
				})
				.switchIfEmpty(chain.filter(exchange));
	}

	/**
	 * Customize codec to handle large binary files
	 */
	@Bean
	CodecCustomizer codecCustomizer() {
		return configurer -> {
			configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024); // 10MB
		};
	}
}