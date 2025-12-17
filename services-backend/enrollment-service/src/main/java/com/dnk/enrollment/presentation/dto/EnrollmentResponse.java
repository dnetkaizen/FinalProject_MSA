package com.dnk.enrollment.presentation.dto;

import com.dnk.enrollment.domain.model.Enrollment;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record EnrollmentResponse(
    UUID id,
    String userId,
    String email,
    UUID courseId,
    Instant enrolledAt
) {
    public static EnrollmentResponse from(Enrollment enrollment) {
        return new EnrollmentResponse(
            enrollment.id(),
            enrollment.userId(),
            enrollment.email(),
            enrollment.courseId(),
            enrollment.enrolledAt()
        );
    }
    
    public static List<EnrollmentResponse> from(List<Enrollment> enrollments) {
        return enrollments.stream()
                .map(EnrollmentResponse::from)
                .toList();
    }
}

