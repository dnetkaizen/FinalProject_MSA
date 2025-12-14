package com.dnk.auth.application.model;

public record GoogleLoginResult(
        String userId,
        String email,
        boolean mfaRequired
) {
}
