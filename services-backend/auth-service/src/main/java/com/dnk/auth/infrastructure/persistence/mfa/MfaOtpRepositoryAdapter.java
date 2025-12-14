package com.dnk.auth.infrastructure.persistence.mfa;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.dnk.auth.application.port.out.MfaOtpRepositoryPort;
import com.dnk.auth.domain.model.MfaOtp;

@Repository
public class MfaOtpRepositoryAdapter implements MfaOtpRepositoryPort {

    private static final Duration DEFAULT_EXPIRATION = Duration.ofMinutes(5);

    private final SpringDataMfaOtpRepository jpaRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public MfaOtpRepositoryAdapter(SpringDataMfaOtpRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    @Transactional
    public MfaOtp save(MfaOtp mfaOtp) {
        Instant now = Instant.now();

        UUID id = mfaOtp.getId() != null ? mfaOtp.getId() : UUID.randomUUID();
        Instant createdAt = mfaOtp.getCreatedAt() != null ? mfaOtp.getCreatedAt() : now;
        Instant expiresAt = mfaOtp.getExpiresAt() != null ? mfaOtp.getExpiresAt() : createdAt.plus(DEFAULT_EXPIRATION);

        String hashedOtp = passwordEncoder.encode(mfaOtp.getOtpHash());

        MfaOtpEntity entity = new MfaOtpEntity();
        entity.setId(id);
        entity.setUserId(mfaOtp.getUserId());
        entity.setEmail(mfaOtp.getEmail());
        entity.setOtpHash(hashedOtp);
        entity.setExpiresAt(expiresAt);
        entity.setVerifiedAt(mfaOtp.getVerifiedAt());
        entity.setCreatedAt(createdAt);

        MfaOtpEntity saved = jpaRepository.save(entity);
        return mapToDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MfaOtp> findValidByUserId(String userId, Instant now) {
        return jpaRepository
                .findFirstByUserIdAndVerifiedAtIsNullAndExpiresAtAfterOrderByCreatedAtDesc(userId, now)
                .map(this::mapToDomain);
    }

    @Override
    @Transactional
    public void markVerified(UUID id, Instant verifiedAt) {
        jpaRepository.updateVerifiedAt(id, verifiedAt);
    }

    @Override
    @Transactional
    public void invalidateAllForUser(String userId) {
        Instant now = Instant.now();
        jpaRepository.invalidateAllForUser(userId, now);
    }

    private MfaOtp mapToDomain(MfaOtpEntity entity) {
        return new MfaOtp(
                entity.getId(),
                entity.getUserId(),
                entity.getEmail(),
                entity.getOtpHash(),
                entity.getExpiresAt(),
                entity.getVerifiedAt(),
                entity.getCreatedAt());
    }
}
