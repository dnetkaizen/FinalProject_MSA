package com.dnk.enrollment.presentation.dto;

import com.dnk.enrollment.domain.model.Course;

import java.util.List;
import java.util.UUID;

public record CourseResponse(
    UUID id,
    String code,
    String name,
    boolean active
) {
    public static CourseResponse from(Course course) {
        return new CourseResponse(
            course.id(),
            course.code(),
            course.name(),
            course.active()
        );
    }
    
    public static List<CourseResponse> from(List<Course> courses) {
        return courses.stream()
                .map(CourseResponse::from)
                .toList();
    }
}

