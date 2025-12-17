package com.dnk.enrollment.application.usecase;

import com.dnk.enrollment.application.exception.ConflictException;
import com.dnk.enrollment.application.port.out.CourseRepositoryPort;
import com.dnk.enrollment.application.port.out.IamServicePort;
import com.dnk.enrollment.domain.model.Course;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateCourseUseCase {

    private final CourseRepositoryPort courseRepository;
    private final IamServicePort iamServicePort;

    @Transactional
    public Course execute(String userId, String code, String name) {
        // Security Check
        boolean isAdmin = iamServicePort.isAdmin(userId);
        boolean hasPerm = iamServicePort.hasPermission(userId, "courses:create");

        if (!isAdmin && !hasPerm) {
            log.warn("User {} attempted to create course without permission", userId);
            throw new AccessDeniedException("Access denied");
        }

        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Course code cannot be empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Course name cannot be empty");
        }

        String trimmedCode = code.trim();
        String trimmedName = name.trim();

        // Conflict Check
        if (courseRepository.existsByCode(trimmedCode)) {
            throw new ConflictException("Course with code " + trimmedCode + " already exists");
        }

        log.info("Creating new course - code: {}, name: {}", trimmedCode, trimmedName);
        Course course = new Course(UUID.randomUUID(), trimmedCode, trimmedName, true);
        Course savedCourse = courseRepository.save(course);
        log.info("Course created successfully - code: {}, name: {}, id: {}", trimmedCode, trimmedName,
                savedCourse.id());
        return savedCourse;
    }
}
