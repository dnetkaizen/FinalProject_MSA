package com.dnk.auth.application.port.out;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import com.dnk.auth.domain.model.MfaOtp;

public interface MfaOtpRepositoryPort {

    MfaOtp save(MfaOtp mfaOtp);

    Optional<MfaOtp> findValidByUserId(String userId, Instant now);

    void markVerified(UUID id, Instant verifiedAt);

    void invalidateAllForUser(String userId);
}
