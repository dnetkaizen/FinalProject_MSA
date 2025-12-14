package com.dnk.auth.application.usecase;

import java.security.SecureRandom;

import com.dnk.auth.application.exception.AuthException;
import com.dnk.auth.application.model.GoogleLoginResult;
import com.dnk.auth.application.model.VerifiedIdentity;
import com.dnk.auth.application.port.out.IdentityProviderPort;
import com.dnk.auth.application.port.out.MfaOtpRepositoryPort;
import com.dnk.auth.domain.model.MfaOtp;

public class GoogleLoginUseCase {

    private final IdentityProviderPort identityProviderPort;
    private final MfaOtpRepositoryPort mfaOtpRepositoryPort;
    private final EmailOtpService emailOtpService;
    private final SecureRandom secureRandom = new SecureRandom();

    public GoogleLoginUseCase(IdentityProviderPort identityProviderPort,
                              MfaOtpRepositoryPort mfaOtpRepositoryPort,
                              EmailOtpService emailOtpService) {
        this.identityProviderPort = identityProviderPort;
        this.mfaOtpRepositoryPort = mfaOtpRepositoryPort;
        this.emailOtpService = emailOtpService;
    }

    public GoogleLoginResult execute(String idToken) {
        VerifiedIdentity identity = identityProviderPort.verifyIdToken(idToken);

        String userId = identity.providerUserId();
        String email = identity.email();
        if (email == null || email.isBlank()) {
            throw new AuthException("Email not available for identity provider user: " + userId);
        }

        // Invalidate previous OTPs for this user
        mfaOtpRepositoryPort.invalidateAllForUser(userId);

        // Generate a new 6-digit OTP
        String otpCode = generateSixDigitOtp();

        // Store OTP (hashing is handled in the infrastructure adapter)
        MfaOtp mfaOtp = new MfaOtp(
                null, // id will be generated
                userId,
                email,
                otpCode, // raw OTP; will be hashed by repository adapter
                null, // expiresAt - defaulted in adapter
                null, // verifiedAt
                null  // createdAt - defaulted in adapter
        );
        mfaOtpRepositoryPort.save(mfaOtp);

        // Send OTP via email
        emailOtpService.sendOtp(email, otpCode);

        return new GoogleLoginResult(userId, email, true);
    }

    private String generateSixDigitOtp() {
        int code = secureRandom.nextInt(1_000_000); // 0..999999
        return String.format("%06d", code);
    }
}
