package com.dnk.auth.application.model;

public record VerifiedIdentity(
        String providerUserId,
        String email,
        boolean emailVerified
) {
}
