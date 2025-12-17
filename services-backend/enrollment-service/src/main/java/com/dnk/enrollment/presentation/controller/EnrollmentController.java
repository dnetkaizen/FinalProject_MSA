package com.dnk.enrollment.presentation.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.dnk.enrollment.application.usecase.EnrollUserUseCase;
import com.dnk.enrollment.application.usecase.ListUserEnrollmentsUseCase;
import com.dnk.enrollment.domain.model.AuthenticatedUser;
import com.dnk.enrollment.domain.model.Enrollment;
import com.dnk.enrollment.presentation.dto.CreateEnrollmentRequest;
import com.dnk.enrollment.presentation.dto.EnrollmentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/enrollments")
@RequiredArgsConstructor
@Validated
public class EnrollmentController {

    private final EnrollUserUseCase enrollUserUseCase;
    private final ListUserEnrollmentsUseCase listUserEnrollmentsUseCase;
    private final com.dnk.enrollment.application.usecase.ListAllEnrollmentsUseCase listAllEnrollmentsUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EnrollmentResponse createEnrollment(@Valid @RequestBody CreateEnrollmentRequest request) {
        AuthenticatedUser user = getAuthenticatedUser();
        Enrollment enrollment = enrollUserUseCase.execute(user.userId(), user.email(), request.courseId());
        return EnrollmentResponse.from(enrollment);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('enrollments:read:me')")
    public List<EnrollmentResponse> getMyEnrollments() {
        String userId = getAuthenticatedUser().userId();
        List<Enrollment> enrollments = listUserEnrollmentsUseCase.execute(userId);
        return EnrollmentResponse.from(enrollments);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('enrollments:read:all')")
    public List<EnrollmentResponse> getAllEnrollments() {
        String userId = getAuthenticatedUser().userId();
        List<Enrollment> enrollments = listAllEnrollmentsUseCase.execute(userId);
        return EnrollmentResponse.from(enrollments);
    }

    private AuthenticatedUser getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("User not authenticated");
        }
        if (authentication.getPrincipal() instanceof AuthenticatedUser) {
            return (AuthenticatedUser) authentication.getPrincipal();
        }
        // Fallback or error if principal is not expected type
        throw new SecurityException("Invalid authentication principal");
    }
}
