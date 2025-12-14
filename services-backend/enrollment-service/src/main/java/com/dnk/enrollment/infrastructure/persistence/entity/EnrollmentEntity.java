package com.dnk.enrollment.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "enrollments", schema = "enrollment_schema")
public class EnrollmentEntity {
    
    @Id
    @Column(name = "id")
    private UUID id;
    
    @Column(name = "user_id", nullable = false)
    private String userId;
    
    @Column(name = "course_id", nullable = false)
    private UUID courseId;
    
    @Column(name = "enrolled_at", nullable = false, updatable = false)
    private Instant enrolledAt;
}

