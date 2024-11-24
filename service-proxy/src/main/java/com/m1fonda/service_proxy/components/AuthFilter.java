package com.m1fonda.service_proxy.components;

import java.util.Map;

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

        log.info("=== Starting request processing ===");
        log.info("Path: {}", path);
        log.info("Headers: {}", exchange.getRequest().getHeaders());

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
                                .doOnNext(body -> log.info("Auth service response body: {}", body));
                    } else {
                        log.error("Auth service returned status: {}", response.statusCode());
                        return Mono.error(new RuntimeException("Auth service validation failed"));
                    }
                })
                .doOnNext(response -> {
                    log.info("Processing auth response: {}", response);
                    log.info("Email from response: {}", response.get("email"));
                    log.info("Roles from response: {}", response.get("roles"));
                })
                .flatMap(response -> {
                    ServerHttpRequest request = exchange.getRequest().mutate()
                            .header("X-User-Email", (String) response.get("email"))
                            .header("X-User-Roles", (String) response.get("roles"))
                            .header(HttpHeaders.AUTHORIZATION, token)
                            .build();

                    log.info("Modified request headers: {}", request.getHeaders());

                    return chain.filter(exchange.mutate().request(request).build())
                            .doOnSuccess(v -> log.info("Request processing completed successfully"))
                            .doOnError(error -> log.error("Error during request processing", error));
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