package com.dnk.iam.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "permissions", schema = "iam_schema")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionEntity {
    
    @Id
    @Column(name = "id")
    private UUID id;
    
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
