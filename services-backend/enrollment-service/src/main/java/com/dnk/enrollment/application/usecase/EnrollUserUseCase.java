package com.dnk.enrollment.application.usecase;

import com.dnk.enrollment.application.exception.EntityNotFoundException;
import com.dnk.enrollment.application.port.out.CourseRepositoryPort;
import com.dnk.enrollment.application.port.out.EnrollmentRepositoryPort;
import com.dnk.enrollment.domain.model.Course;
import com.dnk.enrollment.domain.model.Enrollment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnrollUserUseCase {

    private final EnrollmentRepositoryPort enrollmentRepository;
    private final CourseRepositoryPort courseRepository;

    @Transactional
    public Enrollment execute(String userId, String email, UUID courseId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be empty");
        }
        if (courseId == null) {
            throw new IllegalArgumentException("Course ID cannot be null");
        }

        String trimmedUserId = userId.trim();
        
        // Verify course exists
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found: " + courseId));
        
        // Prevent duplicate enrollment
        if (enrollmentRepository.existsByUserAndCourse(trimmedUserId, courseId)) {
            throw new IllegalArgumentException("User is already enrolled in this course");
        }
        
        log.info("Enrolling user in course - userId: {}, email: {}, courseId: {}, courseCode: {}", 
                trimmedUserId, email, courseId, course.code());
        Enrollment enrollment = new Enrollment(
                UUID.randomUUID(),
                trimmedUserId,
                email,
                courseId,
                Instant.now()
        );
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        log.info("User enrolled successfully - userId: {}, courseId: {}, enrollmentId: {}", 
                trimmedUserId, courseId, savedEnrollment.id());
        return savedEnrollment;
    }
}

