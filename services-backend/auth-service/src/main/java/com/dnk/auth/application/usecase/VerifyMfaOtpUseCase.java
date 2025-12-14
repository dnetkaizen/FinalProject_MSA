package com.dnk.auth.application.usecase;

import java.time.Instant;
import java.util.Optional;

import com.dnk.auth.application.exception.AuthException;
import com.dnk.auth.application.model.AuthTokens;
import com.dnk.auth.application.port.out.MfaOtpRepositoryPort;
import com.dnk.auth.application.port.out.PasswordHashingPort;
import com.dnk.auth.application.port.out.TokenProviderPort;
import com.dnk.auth.domain.model.MfaOtp;

public class VerifyMfaOtpUseCase {

    private final MfaOtpRepositoryPort mfaOtpRepositoryPort;
    private final TokenProviderPort tokenProviderPort;
    private final PasswordHashingPort passwordHashingPort;

    public VerifyMfaOtpUseCase(MfaOtpRepositoryPort mfaOtpRepositoryPort,
                               TokenProviderPort tokenProviderPort,
                               PasswordHashingPort passwordHashingPort) {
        this.mfaOtpRepositoryPort = mfaOtpRepositoryPort;
        this.tokenProviderPort = tokenProviderPort;
        this.passwordHashingPort = passwordHashingPort;
    }

    public AuthTokens execute(String userId, String otp) {
        Instant now = Instant.now();

        Optional<MfaOtp> maybeMfaOtp = mfaOtpRepositoryPort.findValidByUserId(userId, now);
        MfaOtp mfaOtp = maybeMfaOtp.orElseThrow(() -> new AuthException("Invalid or expired OTP"));

        String hashedOtp = mfaOtp.getOtpHash();
        if (hashedOtp == null || !passwordHashingPort.matches(otp, hashedOtp)) {
            throw new AuthException("Invalid OTP");
        }

        mfaOtpRepositoryPort.markVerified(mfaOtp.getId(), now);

        String email = mfaOtp.getEmail();
        String accessToken = tokenProviderPort.generateAccessToken(userId, email);
        String refreshToken = tokenProviderPort.generateRefreshToken(userId, email);

        return new AuthTokens(accessToken, refreshToken);
    }
}
