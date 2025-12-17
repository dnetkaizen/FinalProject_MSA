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

        log.info("JwtTokenProvider initialized with issuer '{}' and accessTokenValidity={} seconds, refreshTokenValidity={} days",
                jwtProperties.getIssuer(), jwtProperties.getExpiration(), REFRESH_TOKEN_VALIDITY.toDays());
    }

    @Override
    public String generateAccessToken(String userId, String email, java.util.List<String> roles, java.util.List<String> permissions) {
        return generateToken(userId, email, roles, permissions, Duration.ofSeconds(jwtProperties.getExpiration()));
    }

    @Override
    public String generateRefreshToken(String userId, String email) {
        return generateToken(userId, email, null, null, REFRESH_TOKEN_VALIDITY);
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public String getUserIdFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(signingKey).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    @Override
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(signingKey).build()
                .parseClaimsJws(token).getBody().get("email", String.class);
    }

    private String generateToken(String userId, String email, java.util.List<String> roles, java.util.List<String> permissions, Duration validity) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(validity);

        var builder = Jwts.builder()
                .setSubject(userId)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiresAt))
                .claim("email", email);

        if (roles != null && !roles.isEmpty()) {
            builder.claim("roles", roles);
        }
        if (permissions != null && !permissions.isEmpty()) {
            builder.claim("permissions", permissions);
        }

        return builder.signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }
}
