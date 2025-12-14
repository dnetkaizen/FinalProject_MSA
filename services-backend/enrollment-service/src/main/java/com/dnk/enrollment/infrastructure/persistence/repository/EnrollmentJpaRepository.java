package com.dnk.enrollment.infrastructure.persistence.repository;

import com.dnk.enrollment.infrastructure.persistence.entity.EnrollmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EnrollmentJpaRepository extends JpaRepository<EnrollmentEntity, UUID> {
    
    @Query("SELECT e FROM EnrollmentEntity e WHERE e.userId = :userId")
    List<EnrollmentEntity> findByUserId(@Param("userId") String userId);
    
    @Query("SELECT COUNT(e) > 0 FROM EnrollmentEntity e WHERE e.userId = :userId AND e.courseId = :courseId")
    boolean existsByUserAndCourse(@Param("userId") String userId, @Param("courseId") UUID courseId);
}

