package com.m1fonda.service_user.components;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class GatewayAuthenticationFilter extends OncePerRequestFilter {
    @Value("${gateway.secret}")
    private String expectedGatewaySecret;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String requestPath = request.getRequestURI();

        if (requestPath.startsWith("/actuator/") ||
                requestPath.startsWith("/v3/api-docs") ||
                requestPath.startsWith("/swagger-ui")) {
            filterChain.doFilter(request, response);
            return;
        }

        String gatewaySecret = request.getHeader("X-Gateway-Secret");

        if (gatewaySecret == null || !gatewaySecret.equals(expectedGatewaySecret)) {
            log.warn("Unauthorized direct access attempt from IP: {} to path: {}",
                    getClientIp(request), requestPath);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"error\":\"Forbidden\",\"message\":\"Direct service access not allowed. Use API Gateway.\"}");
            return;
        }

        String email = request.getHeader("X-User-Email");
        String roles = request.getHeader("X-User-Roles");

        if (email != null && roles != null) {
            List<GrantedAuthority> authorities = Arrays.stream(roles.split(","))
                    .map(String::trim)
                    .filter(role -> !role.isEmpty())
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            UserDetails userDetails = new User(email, "N/A", authorities);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,
                    null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("Authenticated user: {} with authorities: {}", email, authorities);
        } else {
            log.warn("Missing authentication headers from gateway");
        }

        try {
            filterChain.doFilter(request, response);
        } finally {

            SecurityContextHolder.clearContext();
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty()) {
            return ip.split(",")[0].trim();
        }
        ip = request.getHeader("X-Real-IP");
        return ip != null && !ip.isEmpty() ? ip : request.getRemoteAddr();
    }
}