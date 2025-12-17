package com.dnk.enrollment.application.port.out;

import com.dnk.enrollment.domain.model.Enrollment;

import java.util.List;
import java.util.UUID;

public interface EnrollmentRepositoryPort {

    Enrollment save(Enrollment enrollment);

    List<Enrollment> findByUserId(String userId);

    boolean existsByUserAndCourse(String userId, UUID courseId);

    List<Enrollment> findAll();
}
