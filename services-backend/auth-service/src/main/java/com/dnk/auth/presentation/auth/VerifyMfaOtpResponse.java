package com.dnk.auth.presentation.auth;

public record VerifyMfaOtpResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresInSeconds
) {
}
