package com.dnk.auth.infrastructure.persistence.mfa;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataMfaOtpRepository extends JpaRepository<MfaOtpEntity, UUID> {

    Optional<MfaOtpEntity> findFirstByUserIdAndVerifiedAtIsNullAndExpiresAtAfterOrderByCreatedAtDesc(String userId,
            Instant now);

    @Modifying
    @Query("update MfaOtpEntity o set o.verifiedAt = :verifiedAt where o.id = :id")
    void updateVerifiedAt(@Param("id") UUID id, @Param("verifiedAt") Instant verifiedAt);

    @Modifying
    @Query("update MfaOtpEntity o set o.expiresAt = :now where o.userId = :userId and o.verifiedAt is null and o.expiresAt > :now")
    void invalidateAllForUser(@Param("userId") String userId, @Param("now") Instant now);
}
