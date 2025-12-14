package com.dnk.iam.application.usecase;

import com.dnk.iam.application.port.out.PermissionRepositoryPort;
import com.dnk.iam.application.port.out.RolePermissionRepositoryPort;
import com.dnk.iam.application.port.out.RoleRepositoryPort;
import com.dnk.iam.domain.model.Permission;
import com.dnk.iam.domain.model.Role;
import com.dnk.iam.domain.model.RolePermission;
import com.dnk.iam.application.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssignPermissionToRoleUseCase {

    private final RoleRepositoryPort roleRepository;
    private final PermissionRepositoryPort permissionRepository;
    private final RolePermissionRepositoryPort rolePermissionRepository;

    @Transactional
    public void execute(String roleName, String permissionName) {
        if (roleName == null || roleName.trim().isEmpty()) {
            throw new IllegalArgumentException("Role name cannot be empty");
        }
        if (permissionName == null || permissionName.trim().isEmpty()) {
            throw new IllegalArgumentException("Permission name cannot be empty");
        }

        log.info("Assigning permission '{}' to role '{}'", permissionName, roleName);
        Role role = roleRepository.findByName(roleName.trim())
                .orElseThrow(() -> new EntityNotFoundException("Role not found: " + roleName));
                
        Permission permission = permissionRepository.findByName(permissionName.trim())
                .orElseThrow(() -> new EntityNotFoundException("Permission not found: " + permissionName));
        
        rolePermissionRepository.assignPermission(role.id(), permission.id());
    }
}
