package com.dnk.enrollment.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record Enrollment(UUID id, String userId, UUID courseId, Instant enrolledAt) {

    public Enrollment {
        Objects.requireNonNull(id, "Enrollment id must not be null");
        Objects.requireNonNull(userId, "User id must not be null");
        Objects.requireNonNull(courseId, "Course id must not be null");
        Objects.requireNonNull(enrolledAt, "Enrolled at must not be null");
        
        if (userId.isBlank()) {
            throw new IllegalArgumentException("User id must not be empty");
        }
    }
}

