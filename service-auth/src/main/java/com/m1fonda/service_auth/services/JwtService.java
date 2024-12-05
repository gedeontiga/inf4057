package com.m1fonda.service_auth.services;

import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.cglib.core.internal.Function;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.m1fonda.service_auth.customexceptions.TokenExpiredException;
import com.m1fonda.service_auth.entities.Jwt;
import com.m1fonda.service_auth.entities.Users;
import com.m1fonda.service_auth.repositories.JwtRepository;
import com.m1fonda.service_auth.repositories.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Service
public class JwtService {

    private static final String TOKEN_NOT_FOUND = "Token not found";
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(
            Base64.getDecoder().decode("R9MyWaYpeXJwuQmL1nT4HmGQCY0kPj6vF2d3Z5K7A8bNcShEsUgVtWxDfM4BoI9q"));

    private final UserRepository userRepository;
    private final JwtRepository jwtRepository;
    private final String BEARER = "bearer";

    public Map<String, String> generate(final String email) {
        final Users user = userRepository.findByEmailAndEnabledIsTrue(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return this.generateJwt(user);
    }

    public String getEmailFromToken(final String token) {
        return getClaim(token, Claims::getId);
    }

    public Jwt getJwtByToken(final String token) {
        return jwtRepository.findByToken(token).orElseThrow(() -> new RuntimeException(TOKEN_NOT_FOUND));
    }

    public boolean isTokenExpired(String token) {
        final Date expiration = getClaim(token, Claims::getExpiration);

        if (expiration != null && expiration.before(new Date())) {
            throw new TokenExpiredException("Token has expired");
        }
        return false;
    }

    @Scheduled(cron = "@daily")
    public void removeUselessJwt() {
        log.info("Removing token at {}", Instant.now());
        this.jwtRepository.deleteAllByExpiredAtIsBefore(Instant.now());
    }

    private <T> T getClaim(final String token, final Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaims(final String token) throws RuntimeException {
        return Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Map<String, String> generateJwt(final Users user) {

        final Long currentTime = System.currentTimeMillis();
        final Long expirationTime = currentTime + 12 * 3600 * 1000;

        final String bearer = Jwts.builder()
                .claim(Claims.ISSUED_AT, new Date(currentTime))
                .claim(Claims.EXPIRATION, new Date(expirationTime))
                .claim(Claims.ID, user.getEmail())
                .claim(Claims.SUBJECT, user.getRole().getType().toString())
                .signWith(SECRET_KEY)
                .compact();

        Jwt jwt = jwtRepository.save(jwtRepository.findByUserAndExpiredAtIsAfter(user, Instant.now())
                .orElse(
                        Jwt.builder()
                                .token(bearer)
                                .expiredAt(Instant.now().plusMillis(12 * 3600
                                        * 1000))
                                .user(user)
                                .build()));
        return new HashMap<>(Map.of(BEARER, jwt.getToken()));
    }
}