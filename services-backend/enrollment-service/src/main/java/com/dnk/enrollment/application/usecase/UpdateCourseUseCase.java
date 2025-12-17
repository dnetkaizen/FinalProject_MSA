package com.dnk.enrollment.application.usecase;

import com.dnk.enrollment.application.exception.ConflictException;
import com.dnk.enrollment.application.exception.EntityNotFoundException;
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
public class UpdateCourseUseCase {

    private final CourseRepositoryPort courseRepository;
    private final IamServicePort iamServicePort;

    @Transactional
    public Course execute(String userId, UUID courseId, String code, String name, Boolean active) {
        log.info("User {} requesting update for course {}", userId, courseId);

        // Security Check
        boolean isAdmin = iamServicePort.isAdmin(userId);
        boolean hasPerm = iamServicePort.hasPermission(userId, "courses:update");

        if (!isAdmin && !hasPerm) {
            log.warn("Access denied for user {} to update course", userId);
            throw new AccessDeniedException("Access denied");
        }

        // Find existing
        Course existing = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + courseId));

        // Validation
        String newCode = (code != null && !code.isBlank()) ? code.trim() : existing.code();
        String newName = (name != null && !name.isBlank()) ? name.trim() : existing.name();
        boolean newActive = (active != null) ? active : existing.active();

        // Conflict check
        if (!newCode.equals(existing.code()) && courseRepository.existsByCodeAndIdNot(newCode, courseId)) {
            throw new ConflictException("Course with code " + newCode + " already exists");
        }

        Course updated = new Course(courseId, newCode, newName, newActive);
        Course saved = courseRepository.save(updated);

        log.info("Course {} updated by user {}", courseId, userId);
        return saved;
    }
}
