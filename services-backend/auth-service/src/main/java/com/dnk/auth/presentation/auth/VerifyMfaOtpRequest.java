package com.dnk.auth.presentation.auth;

import jakarta.validation.constraints.NotBlank;

public record VerifyMfaOtpRequest(
        @NotBlank String userId,
        @NotBlank String otp
) {
}
