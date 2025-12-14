package com.dnk.iam.application.port.out;

import com.dnk.iam.domain.model.Permission;

import java.util.List;
import java.util.UUID;

public interface RolePermissionRepositoryPort {
    
    void assignPermission(UUID roleId, UUID permissionId);
    
    List<Permission> findPermissionsByRoleId(UUID roleId);
}
