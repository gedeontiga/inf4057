package com.m1fonda.service_agency.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.m1fonda.commons_libs.services.SecurityFilter;

@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain agencyFilterChain(HttpSecurity http,
            SecurityFilter securityFilter) throws Exception {
        http
                .securityMatcher("/api/agency/**")
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth.anyRequest().hasRole("MANAGER")
                        .requestMatchers("/api/demande/**").permitAll());

        return http.build();
    }
}
