package com.dnk.iam.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "role_permissions", schema = "iam_schema")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolePermissionEntity {
    
    @EmbeddedId
    private RolePermissionId id;
    
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
    
    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RolePermissionId implements Serializable {
        private UUID roleId;
        private UUID permissionId;
    }
}
