package com.dnk.enrollment.infrastructure.persistence.repository;

import com.dnk.enrollment.infrastructure.persistence.entity.CourseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CourseJpaRepository extends JpaRepository<CourseEntity, UUID> {
    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, UUID id);

    List<CourseEntity> findByActive(boolean active);
}
