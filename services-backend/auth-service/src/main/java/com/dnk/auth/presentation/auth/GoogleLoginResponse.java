package com.dnk.auth.presentation.auth;

public record GoogleLoginResponse(
        boolean mfaRequired,
        String userId,
        String email
) {
}
