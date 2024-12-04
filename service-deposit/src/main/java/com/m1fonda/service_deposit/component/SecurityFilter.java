// package com.m1fonda.service_deposit.component;

// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.GrantedAuthority;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.core.userdetails.User;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.stereotype.Component;
// import org.springframework.web.filter.OncePerRequestFilter;

// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;

// import java.io.IOException;
// import java.util.Arrays;
// import java.util.List;
// import java.util.stream.Collectors;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// @Component
// @Slf4j
// @RequiredArgsConstructor
// public class SecurityFilter extends OncePerRequestFilter {

//     @Override
//     protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//             throws ServletException, IOException {
//         try {
//             String email = request.getHeader("X-User-Email");
//             String roles = request.getHeader("X-User-Roles");

//             if (email != null && roles != null) {
//                 List<GrantedAuthority> authorities = Arrays.stream(roles.split(","))
//                         .map(role -> {
//                             String authority = role.trim();
//                             // Ensure role has ROLE_ prefix
//                             if (!authority.startsWith("ROLE_")) {
//                                 authority = "ROLE_" + authority;
//                             }
//                             log.debug("Adding authority: {}", authority);
//                             return new SimpleGrantedAuthority(authority);
//                         })
//                         .collect(Collectors.toList());

//                 log.debug("Created authorities for {}: {}", email, authorities);

//                 // Use Spring Security's User class
//                 UserDetails userDetails = new User(
//                         email, // username (email in this case)
//                         "N/A", // password (not needed for token auth)
//                         true, // enabled
//                         true, // accountNonExpired
//                         true, // credentialsNonExpired
//                         true, // accountNonLocked
//                         authorities);

//                 Authentication authentication = new UsernamePasswordAuthenticationToken(
//                         userDetails, null, authorities);
//                 SecurityContextHolder.getContext().setAuthentication(authentication);

//                 log.debug("Set authentication in SecurityContext for user: {}", email);
//                 filterChain.doFilter(request, response);
//             } else {
//                 log.error("Missing required headers - Email: {}, Roles: {}", email, roles);
//                 response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//             }
//         } catch (Exception e) {
//             log.error("Error during authentication setup", e);
//             response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//             SecurityContextHolder.clearContext();
//         }
//     }
// }