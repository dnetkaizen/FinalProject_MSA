package com.dnk.enrollment.application.usecase;

import com.dnk.enrollment.application.port.out.EnrollmentRepositoryPort;
import com.dnk.enrollment.application.port.out.IamServicePort;
import com.dnk.enrollment.domain.model.Enrollment;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ListAllEnrollmentsUseCase {

    private final EnrollmentRepositoryPort enrollmentRepository;
    private final IamServicePort iamServicePort;

    @Transactional(readOnly = true)
    public List<Enrollment> execute(String userId) {
        log.info("Checking authorization for user '{}' to view all enrollments", userId);

        boolean isAdmin = iamServicePort.isAdmin(userId);
        // Also check granular permission
        boolean hasPermission = iamServicePort.hasPermission(userId, "enrollments:read:all");

        log.info("User '{}': isAdmin={}, hasPermission={}", userId, isAdmin, hasPermission);

        if (!isAdmin && !hasPermission) {
            log.warn("Access denied for user '{}'. Requirements: Role 'admin' OR Permission 'enrollments:read:all'",
                    userId);
            throw new AccessDeniedException("User " + userId + " is not authorized to view all enrollments.");
        }

        List<Enrollment> enrollments = enrollmentRepository.findAll();
        log.info("Access granted. Returning {} enrollments for user '{}'", enrollments.size(), userId);
        return enrollments;
    }
}
