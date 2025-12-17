package com.dnk.enrollment.application.port.out;

import com.dnk.enrollment.domain.model.Course;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CourseRepositoryPort {

    Course save(Course course);

    List<Course> findAll();

    List<Course> findByActive(boolean active);

    Optional<Course> findById(UUID id);

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, UUID id);
}
