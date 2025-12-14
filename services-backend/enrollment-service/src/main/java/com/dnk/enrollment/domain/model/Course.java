package com.dnk.enrollment.domain.model;

import java.util.Objects;
import java.util.UUID;

public record Course(UUID id, String code, String name, boolean active) {

    public Course {
        Objects.requireNonNull(id, "Course id must not be null");
        Objects.requireNonNull(code, "Course code must not be null");
        Objects.requireNonNull(name, "Course name must not be null");
        
        if (code.isBlank()) {
            throw new IllegalArgumentException("Course code must not be empty");
        }
        if (name.isBlank()) {
            throw new IllegalArgumentException("Course name must not be empty");
        }
    }
}

