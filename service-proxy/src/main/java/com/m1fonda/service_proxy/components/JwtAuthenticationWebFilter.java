package com.m1fonda.service_proxy.components;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.m1fonda.service_proxy.services.JwtService;

import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationWebFilter implements WebFilter {
    private final JwtService jwtService;
    private final ReactiveUserDetailsService userDetailsService;

    @Override
    public @NonNull Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        // Skip authentication for public paths
        if (isPublicPath(path)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        // Missing Authorization header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Mono.error(new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authentication credentials are required"));
        }

        String jwt = authHeader.substring(7);

        try {
            // Validate token format first
            if (!isValidTokenFormat(jwt)) {
                return Mono.error(new JwtException("Invalid token format"));
            }

            // Check if token is expired
            if (jwtService.isTokenExpired(jwt)) {
                return Mono.error(new JwtException("Token has expired"));
            }

            // Validate token completely
            if (!jwtService.isTokenValid(jwt)) {
                return Mono.error(new JwtException("Invalid token"));
            }

            // Extract email and authenticate
            String email = jwtService.getEmailFromToken(jwt);

            if (email == null || email.trim().isEmpty()) {
                return Mono.error(new JwtException("Invalid token: missing email"));
            }

            return userDetailsService.findByUsername(email)
                    .flatMap(userDetails -> {
                        var authToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());

                        log.debug("Authenticated user: {} with roles: {}",
                                email, userDetails.getAuthorities());

                        return chain.filter(exchange)
                                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authToken));
                    })
                    .switchIfEmpty(Mono.error(new ResponseStatusException(
                            HttpStatus.UNAUTHORIZED,
                            "User not found")));

        } catch (JwtException e) {
            log.warn("JWT validation failed: {}", e.getMessage());
            return Mono.error(e);
        } catch (Exception e) {
            log.error("Unexpected error during authentication: {}", e.getMessage(), e);
            return Mono.error(new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Authentication processing failed"));
        }
    }

    private boolean isPublicPath(String path) {
        return path.startsWith("/api/auth/") ||
                path.startsWith("/actuator/health") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/webjars/") ||
                path.startsWith("/ws/");
    }

    private boolean isValidTokenFormat(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        String[] parts = token.split("\\.");
        return parts.length == 3;
    }
}