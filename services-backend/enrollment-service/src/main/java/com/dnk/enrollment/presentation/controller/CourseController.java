package com.dnk.enrollment.presentation.controller;

import com.dnk.enrollment.application.usecase.CreateCourseUseCase;
import com.dnk.enrollment.application.usecase.ListCoursesUseCase;
import com.dnk.enrollment.domain.model.Course;
import com.dnk.enrollment.presentation.dto.CourseResponse;
import com.dnk.enrollment.presentation.dto.CreateCourseRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import com.dnk.enrollment.application.usecase.DeleteCourseUseCase;
import com.dnk.enrollment.application.usecase.GetCourseByIdUseCase;
import com.dnk.enrollment.application.usecase.UpdateCourseUseCase;
import com.dnk.enrollment.presentation.dto.UpdateCourseRequest;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CreateCourseUseCase createCourseUseCase;
    private final ListCoursesUseCase listCoursesUseCase;
    private final GetCourseByIdUseCase getCourseByIdUseCase;
    private final UpdateCourseUseCase updateCourseUseCase;
    private final DeleteCourseUseCase deleteCourseUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('courses:create')")
    public CourseResponse createCourse(@Valid @RequestBody CreateCourseRequest request) {
        String userId = getAuthenticatedUserId();
        Course course = createCourseUseCase.execute(userId, request.code(), request.name());
        return CourseResponse.from(course);
    }

    private String getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("User not authenticated");
        }
        return authentication.getName();
    }

    @GetMapping
    public List<CourseResponse> listCourses(@RequestParam(required = false) Boolean active) {
        List<Course> courses = listCoursesUseCase.execute(active);
        return CourseResponse.from(courses);
    }

    @GetMapping("/{id}")
    public CourseResponse getCourse(@PathVariable UUID id) {
        return CourseResponse.from(getCourseByIdUseCase.execute(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('courses:update')")
    public CourseResponse updateCourse(@PathVariable UUID id, @RequestBody UpdateCourseRequest request) {
        String userId = getAuthenticatedUserId();
        Course course = updateCourseUseCase.execute(userId, id, request.code(), request.name(), request.active());
        return CourseResponse.from(course);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('courses:delete')")
    public void deleteCourse(@PathVariable UUID id) {
        String userId = getAuthenticatedUserId();
        deleteCourseUseCase.execute(userId, id);
    }
}
