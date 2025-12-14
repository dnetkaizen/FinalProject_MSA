package com.dnk.enrollment.application.usecase;

import com.dnk.enrollment.application.port.out.EnrollmentRepositoryPort;
import com.dnk.enrollment.domain.model.Enrollment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListUserEnrollmentsUseCase {

    private final EnrollmentRepositoryPort enrollmentRepository;

    public List<Enrollment> execute(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be empty");
        }

        String trimmedUserId = userId.trim();
        log.debug("Fetching enrollments for user: {}", trimmedUserId);
        return enrollmentRepository.findByUserId(trimmedUserId);
    }
}

