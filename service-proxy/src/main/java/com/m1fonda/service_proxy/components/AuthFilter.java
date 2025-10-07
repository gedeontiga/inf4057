package com.m1fonda.service_proxy.components;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthFilter implements GatewayFilter, Ordered {
    private final WebClient.Builder webClientBuilder;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String path = exchange.getRequest().getPath().value();

        if (token == null || !token.startsWith("Bearer ")) {
            log.error("No valid authorization token found for path: {}", path);
            return unauthorized(exchange);
        }

        return webClientBuilder.build()
                .post()
                .uri("http://service-auth/api/auth/validate")
                .header(HttpHeaders.AUTHORIZATION, token)
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(Map.class)
                                .doOnNext(body -> log.debug("Auth service response body: {}", body))
                                .flatMap(body -> {
                                    if (body.get("email") == null || body.get("roles") == null) {
                                        log.error("Invalid auth response format: missing email or roles");
                                        return Mono.error(new RuntimeException("Invalid auth response format"));
                                    }
                                    return Mono.just(body);
                                });
                    } else {
                        log.error("Auth service returned status: {}", response.statusCode());
                        return Mono.error(new RuntimeException("Auth service validation failed"));
                    }
                })
                .flatMap(response -> {
                    String email = (String) response.get("email");
                    String roles = (String) response.get("roles");

                    // Normalize roles format
                    roles = Arrays.stream(roles.split(","))
                            .map(role -> {
                                role = role.trim();
                                if (!role.startsWith("ROLE_")) {
                                    role = "ROLE_" + role;
                                }
                                return role;
                            })
                            .collect(Collectors.joining(","));

                    ServerHttpRequest request = exchange.getRequest().mutate()
                            .header("X-User-Email", email)
                            .header("X-User-Roles", roles)
                            .header(HttpHeaders.AUTHORIZATION, token)
                            .build();

                    log.debug("Forwarding request with headers - Email: {}, Roles: {}", email, roles);
                    return chain.filter(exchange.mutate().request(request).build());
                })
                .onErrorResume(error -> {
                    log.error("Error during authentication process", error);
                    return unauthorized(exchange);
                });
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}