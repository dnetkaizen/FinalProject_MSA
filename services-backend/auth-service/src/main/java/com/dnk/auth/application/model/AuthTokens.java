package com.dnk.auth.application.model;

public record AuthTokens(
        String accessToken,
        String refreshToken
) {
}
