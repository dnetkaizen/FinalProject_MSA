package com.dnk.enrollment.application.usecase;

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
public class DeleteCourseUseCase {

    private final CourseRepositoryPort courseRepository;
    private final IamServicePort iamServicePort;

    @Transactional
    public void execute(String userId, UUID courseId) {
        log.info("User {} requesting deletion (soft) for course {}", userId, courseId);

        // Security Check
        boolean isAdmin = iamServicePort.isAdmin(userId);
        boolean hasPerm = iamServicePort.hasPermission(userId, "courses:delete");

        if (!isAdmin && !hasPerm) {
            log.warn("Access denied for user {} to delete course", userId);
            throw new AccessDeniedException("Access denied");
        }

        Course existing = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + courseId));

        // Soft Delete
        Course deactivated = new Course(existing.id(), existing.code(), existing.name(), false);
        courseRepository.save(deactivated);

        log.info("Course {} deactivated by user {}", courseId, userId);
    }
}
