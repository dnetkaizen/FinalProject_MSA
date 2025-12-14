package com.dnk.enrollment.presentation.controller;

import com.dnk.enrollment.application.usecase.CreateCourseUseCase;
import com.dnk.enrollment.application.usecase.ListCoursesUseCase;
import com.dnk.enrollment.domain.model.Course;
import com.dnk.enrollment.presentation.dto.CourseResponse;
import com.dnk.enrollment.presentation.dto.CreateCourseRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CreateCourseUseCase createCourseUseCase;
    private final ListCoursesUseCase listCoursesUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CourseResponse createCourse(@Valid @RequestBody CreateCourseRequest request) {
        Course course = createCourseUseCase.execute(request.code(), request.name());
        return CourseResponse.from(course);
    }

    @GetMapping
    public List<CourseResponse> listCourses() {
        List<Course> courses = listCoursesUseCase.execute();
        return CourseResponse.from(courses);
    }
}

