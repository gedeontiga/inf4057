package com.m1fonda.service_user.services;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.m1fonda.service_user.entities.Users;

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

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String email = request.getHeader("X-User-Email");
        String roles = request.getHeader("X-User-Roles");
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        log.info("=== Authentication Headers ===");
        log.info("Email from header: {}", email);
        log.info("Roles from header: {}", roles);
        log.info("Authorization header: {}", authHeader);

        if (email != null && roles != null) {
            try {
                List<GrantedAuthority> authorities = Arrays.stream(roles.split(","))
                        .map(role -> {
                            String authority = "ROLE_" + role.trim();
                            log.info("Adding authority: {}", authority);
                            return new SimpleGrantedAuthority(authority);
                        })
                        .collect(Collectors.toList());

                log.info("Created authorities: {}", authorities);

                Users user = Users.builder()
                        .email(email)
                        .build();

                Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.info("Authentication set in SecurityContext: {}", authentication);
                log.info("Current user authorities: {}", authentication.getAuthorities());
            } catch (Exception e) {
                log.error("Error during authentication setup", e);
            }
        } else {
            log.error("Missing required headers - Email: {}, Roles: {}", email, roles);
        }

        try {
            log.info("=== Pre-FilterChain Authentication State ===");
            Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
            log.info("Current Authentication: {}", currentAuth);
            if (currentAuth != null) {
                log.info("Principal: {}", currentAuth.getPrincipal());
                log.info("Authorities: {}", currentAuth.getAuthorities());
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Error during filter chain execution", e);
            throw e;
        } finally {
            log.info("=== Security Filter Processing End ===");
        }
    }
}