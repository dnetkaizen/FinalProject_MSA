package com.dnk.enrollment.application.usecase;

import com.dnk.enrollment.application.port.out.CourseRepositoryPort;
import com.dnk.enrollment.domain.model.Course;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateCourseUseCase {

    private final CourseRepositoryPort courseRepository;

    @Transactional
    public Course execute(String code, String name) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Course code cannot be empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Course name cannot be empty");
        }

        String trimmedCode = code.trim();
        String trimmedName = name.trim();
        
        log.info("Creating new course - code: {}, name: {}", trimmedCode, trimmedName);
        Course course = new Course(UUID.randomUUID(), trimmedCode, trimmedName, true);
        Course savedCourse = courseRepository.save(course);
        log.info("Course created successfully - code: {}, name: {}, id: {}", trimmedCode, trimmedName, savedCourse.id());
        return savedCourse;
    }
}

