package com.dnk.enrollment.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateEnrollmentRequest(
    @NotBlank(message = "User ID is required")
    String userId,
    
    @NotNull(message = "Course ID is required")
    UUID courseId
) {}

