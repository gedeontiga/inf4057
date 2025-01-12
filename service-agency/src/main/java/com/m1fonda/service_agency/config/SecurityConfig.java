package com.m1fonda.service_agency.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.m1fonda.service_agency.components.SecurityFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
        @Bean
        SecurityFilterChain agencyFilterChain(HttpSecurity http,
                        SecurityFilter securityFilter) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(
                                                authorize -> authorize
                                                                .requestMatchers("/api/demande/**").permitAll()
                                                                .requestMatchers("/api/agency/**").authenticated())
                                .sessionManagement(
                                                httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer
                                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}
