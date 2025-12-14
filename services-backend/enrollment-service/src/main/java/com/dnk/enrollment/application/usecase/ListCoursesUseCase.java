package com.dnk.enrollment.application.usecase;

import com.dnk.enrollment.application.port.out.CourseRepositoryPort;
import com.dnk.enrollment.domain.model.Course;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListCoursesUseCase {

    private final CourseRepositoryPort courseRepository;

    public List<Course> execute() {
        log.debug("Fetching all courses");
        return courseRepository.findAll();
    }
}

