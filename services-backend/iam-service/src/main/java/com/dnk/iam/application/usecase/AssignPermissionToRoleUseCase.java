package com.dnk.iam.application.usecase;

import com.dnk.iam.application.port.out.PermissionRepositoryPort;
import com.dnk.iam.application.port.out.RolePermissionRepositoryPort;
import com.dnk.iam.application.port.out.RoleRepositoryPort;
import com.dnk.iam.domain.model.Permission;
import com.dnk.iam.domain.model.Role;
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

        String trimmedRoleName = roleName.trim();
        String trimmedPermissionName = permissionName.trim();
        
        log.info("AUDIT: Assigning permission to role - roleName: {}, permissionName: {}", trimmedRoleName, trimmedPermissionName);
        Role role = roleRepository.findByName(trimmedRoleName)
                .orElseThrow(() -> new EntityNotFoundException("Role not found: " + trimmedRoleName));
                
        Permission permission = permissionRepository.findByName(trimmedPermissionName)
                .orElseThrow(() -> new EntityNotFoundException("Permission not found: " + trimmedPermissionName));
        
        rolePermissionRepository.assignPermission(role.id(), permission.id());
        log.info("AUDIT: Permission assigned to role successfully - roleName: {}, permissionName: {}", trimmedRoleName, trimmedPermissionName);
    }
}
