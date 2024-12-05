package com.m1fonda.service_proxy.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import com.m1fonda.service_proxy.components.AuthFilter;

@Configuration
public class GatewayConfig {
        @Bean
        RouteLocator routes(RouteLocatorBuilder builder, AuthFilter authFilter) {
                return builder.routes()
                                .route("service-auth", r -> r
                                                .path("/api/auth/**")
                                                .uri("lb://service-auth"))
                                .route("service-agency", r -> r
                                                .path("/api/demande/**")
                                                .uri("lb://service-agency"))
                                .route("service-user", r -> r
                                                .path("/api/user/**", "/api/manager/**", "/api/admin/**")
                                                .filters(f -> f.filter(authFilter))
                                                .uri("lb://service-user"))
                                .route("service-account", r -> r
                                                .path("/api/account/**")
                                                .filters(f -> f.filter(authFilter))
                                                .uri("lb://service-account"))
                                .route("service-agency", r -> r
                                                .path("/api/agency/**")
                                                .filters(f -> f.filter(authFilter))
                                                .uri("lb://service-agency"))
                                .route("service-bank", r -> r
                                                .path("/api/bank/**")
                                                .filters(f -> f.filter(authFilter))
                                                .uri("lb://service-bank"))
                                .route("service-deposit", r -> r
                                                .path("/api/deposit/**")
                                                .filters(f -> f.filter(authFilter))
                                                .uri("lb://service-deposit"))
                                .route("service-withdrawal", r -> r
                                                .path("/api/withdrawal/**")
                                                .filters(f -> f.filter(authFilter))
                                                .uri("lb://service-withdrawal"))
                                .route("service-transfer", r -> r
                                                .path("/api/transfer/**")
                                                .filters(f -> f.filter(authFilter))
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