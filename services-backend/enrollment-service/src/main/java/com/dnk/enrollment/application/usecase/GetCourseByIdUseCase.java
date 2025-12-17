package com.dnk.enrollment.application.usecase;

import com.dnk.enrollment.application.exception.EntityNotFoundException;
import com.dnk.enrollment.application.port.out.CourseRepositoryPort;
import com.dnk.enrollment.domain.model.Course;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetCourseByIdUseCase {

    private final CourseRepositoryPort courseRepository;

    @Transactional(readOnly = true)
    public Course execute(UUID id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + id));
    }
}
