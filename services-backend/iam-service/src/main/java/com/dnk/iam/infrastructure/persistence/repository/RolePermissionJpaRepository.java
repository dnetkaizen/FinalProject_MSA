package com.dnk.iam.infrastructure.persistence.repository;

import com.dnk.iam.infrastructure.persistence.entity.RolePermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RolePermissionJpaRepository extends JpaRepository<RolePermissionEntity, RolePermissionEntity.RolePermissionId> {
    
    @Query("SELECT rp FROM RolePermissionEntity rp WHERE rp.id.roleId = :roleId")
    List<RolePermissionEntity> findByRoleId(@Param("roleId") UUID roleId);
}
