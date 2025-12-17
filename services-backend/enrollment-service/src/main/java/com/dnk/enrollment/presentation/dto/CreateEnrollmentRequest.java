package com.dnk.enrollment.presentation.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateEnrollmentRequest(
        @NotNull(message = "Course ID is required") UUID courseId) {
}
