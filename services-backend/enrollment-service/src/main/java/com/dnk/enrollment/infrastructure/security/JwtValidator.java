package com.dnk.enrollment.infrastructure.security;

import com.dnk.enrollment.domain.model.AuthenticatedUser;
import com.dnk.enrollment.infrastructure.config.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import jakarta.annotation.PostConstruct;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtValidator {

    private final JwtProperties jwtProperties;
    private Key signingKey;

    @PostConstruct
    public void init() {
        // Convert the secret key string to bytes and create a secure key
        this.signingKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public AuthenticatedUser validateAndExtractUser(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new SecurityException("Invalid token: Must start with 'Bearer '");
        }

        try {
            // Remove 'Bearer ' prefix
            String jwtToken = token.substring(7);
            
            // Parse and validate the token
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(jwtToken);

            Claims claims = claimsJws.getBody();

            // Validate issuer
            if (!jwtProperties.getIssuer().equals(claims.getIssuer())) {
                throw new SecurityException("Invalid token: Invalid issuer");
            }

            // Check expiration
            if (claims.getExpiration().before(new Date())) {
                throw new SecurityException("Token has expired");
            }

            // Extract user information
            String userId = claims.getSubject();
            String email = claims.get("email", String.class);

            if (userId == null || email == null) {
                throw new SecurityException("Invalid token: Missing required claims");
            }

            return new AuthenticatedUser(userId, email);

        } catch (ExpiredJwtException ex) {
            throw new SecurityException("Token has expired: " + ex.getMessage(), ex);
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException ex) {
            throw new SecurityException("Invalid token: " + ex.getMessage(), ex);
        } catch (Exception ex) {
            log.error("Error validating token", ex);
            throw new SecurityException("Error validating token: " + ex.getMessage(), ex);
        }
    }
}

