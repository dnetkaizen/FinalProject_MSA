package com.dnk.iam.application.usecase;

import com.dnk.iam.application.port.out.PermissionRepositoryPort;
import com.dnk.iam.domain.model.Permission;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreatePermissionUseCase {

    private final PermissionRepositoryPort permissionRepository;

    @Transactional
    public Permission execute(String permissionName) {
        if (permissionName == null || permissionName.trim().isEmpty()) {
            throw new IllegalArgumentException("Permission name cannot be empty");
        }
        
        String trimmedPermissionName = permissionName.trim();
        log.info("AUDIT: Creating new permission - permissionName: {}", trimmedPermissionName);
        Permission permission = new Permission(UUID.randomUUID(), trimmedPermissionName);
        Permission savedPermission = permissionRepository.save(permission);
        log.info("AUDIT: Permission created successfully - permissionName: {}, permissionId: {}", trimmedPermissionName, savedPermission.id());
        return savedPermission;
    }
}
