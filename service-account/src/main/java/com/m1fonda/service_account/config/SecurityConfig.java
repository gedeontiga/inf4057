package com.m1fonda.service_account.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.m1fonda.service_account.components.SecurityFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Bean
        SecurityFilterChain publicFilterChain(HttpSecurity http,
                        SecurityFilter securityFilter) throws Exception {
                return http
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(authorize -> authorize
                                                .requestMatchers("/api/account/**").authenticated())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                                .build();
        }
}
