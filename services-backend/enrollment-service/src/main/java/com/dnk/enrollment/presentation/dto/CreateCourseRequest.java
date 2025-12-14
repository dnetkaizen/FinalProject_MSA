package com.dnk.enrollment.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCourseRequest(
    @NotBlank(message = "Course code is required")
    String code,
    
    @NotBlank(message = "Course name is required")
    String name
) {}

