package com.dnk.enrollment.presentation.dto;

public record UpdateCourseRequest(
        String code,
        String name,
        Boolean active) {
}
