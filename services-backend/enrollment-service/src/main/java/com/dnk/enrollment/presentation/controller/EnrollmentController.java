package com.dnk.enrollment.presentation.controller;

import com.dnk.enrollment.application.usecase.EnrollUserUseCase;
import com.dnk.enrollment.application.usecase.ListUserEnrollmentsUseCase;
import com.dnk.enrollment.domain.model.Enrollment;
import com.dnk.enrollment.presentation.dto.CreateEnrollmentRequest;
import com.dnk.enrollment.presentation.dto.EnrollmentResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EnrollmentResponse createEnrollment(@Valid @RequestBody CreateEnrollmentRequest request) {
        Enrollment enrollment = enrollUserUseCase.execute(request.userId(), request.courseId());
        return EnrollmentResponse.from(enrollment);
    }

    @GetMapping("/user/{userId}")
    public List<EnrollmentResponse> listUserEnrollments(
            @PathVariable @NotBlank String userId
    ) {
        List<Enrollment> enrollments = listUserEnrollmentsUseCase.execute(userId);
        return EnrollmentResponse.from(enrollments);
    }
}

