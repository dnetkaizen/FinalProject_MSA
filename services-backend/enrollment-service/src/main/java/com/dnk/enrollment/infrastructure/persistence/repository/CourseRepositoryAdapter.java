package com.dnk.enrollment.infrastructure.persistence.repository;

import com.dnk.enrollment.application.port.out.CourseRepositoryPort;
import com.dnk.enrollment.domain.model.Course;
import com.dnk.enrollment.infrastructure.persistence.entity.CourseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CourseRepositoryAdapter implements CourseRepositoryPort {

    private final CourseJpaRepository courseJpaRepository;

    @Override
    public Course save(Course course) {
        CourseEntity entity = CourseEntity.builder()
                .id(course.id())
                .code(course.code())
                .name(course.name())
                .active(course.active())
                .build();
        CourseEntity saved = courseJpaRepository.save(entity);
        return new Course(saved.getId(), saved.getCode(), saved.getName(), saved.isActive());
    }

    @Override
    public List<Course> findAll() {
        return courseJpaRepository.findAll().stream()
                .map(entity -> new Course(entity.getId(), entity.getCode(), entity.getName(), entity.isActive()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Course> findById(UUID id) {
        return courseJpaRepository.findById(id)
                .map(entity -> new Course(entity.getId(), entity.getCode(), entity.getName(), entity.isActive()));
    }
}

