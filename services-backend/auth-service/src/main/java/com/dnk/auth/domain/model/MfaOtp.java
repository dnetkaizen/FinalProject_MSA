package com.dnk.auth.domain.model;

import java.time.Instant;
import java.util.UUID;

public class MfaOtp {

    private final UUID id;
    private final String userId;
    private final String email;
    private final String otpHash;
    private final Instant expiresAt;
    private final Instant verifiedAt;
    private final Instant createdAt;

    public MfaOtp(UUID id,
                  String userId,
                  String email,
                  String otpHash,
                  Instant expiresAt,
                  Instant verifiedAt,
                  Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.email = email;
        this.otpHash = otpHash;
        this.expiresAt = expiresAt;
        this.verifiedAt = verifiedAt;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getOtpHash() {
        return otpHash;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getVerifiedAt() {
        return verifiedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
