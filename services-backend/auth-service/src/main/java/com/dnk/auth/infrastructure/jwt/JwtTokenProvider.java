package com.dnk.auth.infrastructure.jwt;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.dnk.auth.application.port.out.TokenProviderPort;
import com.dnk.auth.infrastructure.config.JwtProperties;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider implements TokenProviderPort {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);

    private static final Duration ACCESS_TOKEN_VALIDITY = Duration.ofMinutes(15);
    private static final Duration REFRESH_TOKEN_VALIDITY = Duration.ofDays(7);

    private final JwtProperties jwtProperties;
    private Key signingKey;

    @PostConstruct
    private void init() {
        try {
            this.signingKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException("Invalid JWT secret: " + ex.getMessage(), ex);
        }

        log.info("JwtTokenProvider initialized with issuer '{}' and accessTokenValidity={} minutes, refreshTokenValidity={} days",
                jwtProperties.getIssuer(), ACCESS_TOKEN_VALIDITY.toMinutes(), REFRESH_TOKEN_VALIDITY.toDays());
    }

    @Override
    public String generateAccessToken(String userId, String email) {
        return generateToken(userId, email, ACCESS_TOKEN_VALIDITY);
    }

    @Override
    public String generateRefreshToken(String userId, String email) {
        return generateToken(userId, email, REFRESH_TOKEN_VALIDITY);
    }

    private String generateToken(String userId, String email, Duration validity) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(validity);

        return Jwts.builder()
                .setSubject(userId)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiresAt))
                .claim("email", email)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }
}
