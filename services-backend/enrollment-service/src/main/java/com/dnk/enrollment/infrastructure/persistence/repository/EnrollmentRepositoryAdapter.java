package com.dnk.enrollment.infrastructure.persistence.repository;

import com.dnk.enrollment.application.port.out.EnrollmentRepositoryPort;
import com.dnk.enrollment.domain.model.Enrollment;
import com.dnk.enrollment.infrastructure.persistence.entity.EnrollmentEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class EnrollmentRepositoryAdapter implements EnrollmentRepositoryPort {

    private final EnrollmentJpaRepository enrollmentJpaRepository;

    @Override
    public Enrollment save(Enrollment enrollment) {
        EnrollmentEntity entity = EnrollmentEntity.builder()
                .id(enrollment.id())
                .userId(enrollment.userId())
                .email(enrollment.email())
                .courseId(enrollment.courseId())
                .enrolledAt(enrollment.enrolledAt())
                .build();
        EnrollmentEntity saved = enrollmentJpaRepository.save(entity);
        return new Enrollment(saved.getId(), saved.getUserId(), saved.getEmail(), saved.getCourseId(), saved.getEnrolledAt());
    }

    @Override
    public List<Enrollment> findByUserId(String userId) {
        return enrollmentJpaRepository.findByUserId(userId).stream()
                .map(entity -> new Enrollment(entity.getId(), entity.getUserId(), entity.getEmail(), entity.getCourseId(),
                        entity.getEnrolledAt()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByUserAndCourse(String userId, UUID courseId) {
        return enrollmentJpaRepository.existsByUserAndCourse(userId, courseId);
    }

    @Override
    public List<Enrollment> findAll() {
        return enrollmentJpaRepository.findAll().stream()
                .map(entity -> new Enrollment(entity.getId(), entity.getUserId(), entity.getEmail(), entity.getCourseId(),
                        entity.getEnrolledAt()))
                .collect(Collectors.toList());
    }
}
