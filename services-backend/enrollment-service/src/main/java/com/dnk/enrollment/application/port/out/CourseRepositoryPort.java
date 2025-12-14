package com.dnk.enrollment.application.port.out;

import com.dnk.enrollment.domain.model.Course;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CourseRepositoryPort {
    
    Course save(Course course);
    
    List<Course> findAll();
    
    Optional<Course> findById(UUID id);
}

