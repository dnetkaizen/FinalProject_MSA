package com.dnk.auth.presentation.auth;

import jakarta.validation.constraints.NotBlank;

public record RefreshSessionRequest(
    @NotBlank String refreshToken
) {}
