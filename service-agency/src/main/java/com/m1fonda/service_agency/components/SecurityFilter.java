package com.m1fonda.service_agency.components;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    @Value("${gateway.secret}")
    private String expectedGatewaySecret;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            String gatewaySecret = request.getHeader("X-Gateway-Secret");

            if (gatewaySecret == null || !gatewaySecret.equals(expectedGatewaySecret)) {
                log.error("Invalid or missing gateway secret from IP: {}",
                        request.getRemoteAddr());
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("{\"error\":\"Direct access not allowed\"}");
                return;
            }

            // Get user info from headers (trusted because gateway secret is valid)
            String email = request.getHeader("X-User-Email");
            String roles = request.getHeader("X-User-Roles");

            if (email != null && roles != null) {
                List<GrantedAuthority> authorities = Arrays.stream(roles.split(","))
                        .map(role -> {
                            String authority = role.trim();
                            if (!authority.startsWith("ROLE_")) {
                                authority = "ROLE_" + authority;
                            }
                            return new SimpleGrantedAuthority(authority);
                        })
                        .collect(Collectors.toList());

                log.debug("Created authorities for {}: {}", email, authorities);

                UserDetails userDetails = new User(
                        email,
                        "N/A", // No password needed (already authenticated by gateway)
                        true, true, true, true,
                        authorities);

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        authorities);

                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("Set authentication for user: {}", email);

                filterChain.doFilter(request, response);
            } else {
                log.error("Missing user headers from gateway");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\":\"Missing authentication headers\"}");
            }
        } catch (Exception e) {
            log.error("Error during authentication setup", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            SecurityContextHolder.clearContext();
        }
    }
}