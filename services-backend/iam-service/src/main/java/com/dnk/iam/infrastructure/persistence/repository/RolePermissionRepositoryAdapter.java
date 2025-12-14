package com.dnk.iam.infrastructure.persistence.repository;

import com.dnk.iam.application.port.out.RolePermissionRepositoryPort;
import com.dnk.iam.domain.model.Permission;
import com.dnk.iam.infrastructure.persistence.entity.RolePermissionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class RolePermissionRepositoryAdapter implements RolePermissionRepositoryPort {

    private final RolePermissionJpaRepository rolePermissionJpaRepository;
    private final PermissionJpaRepository permissionJpaRepository;

    @Override
    public void assignPermission(UUID roleId, UUID permissionId) {
        RolePermissionEntity.RolePermissionId id = new RolePermissionEntity.RolePermissionId(roleId, permissionId);
        RolePermissionEntity entity = new RolePermissionEntity();
        entity.setId(id);
        rolePermissionJpaRepository.save(entity);
    }

    @Override
    public List<Permission> findPermissionsByRoleId(UUID roleId) {
        return rolePermissionJpaRepository.findByRoleId(roleId).stream()
                .map(entity -> entity.getId().getPermissionId())
                .map(permissionId -> permissionJpaRepository.findById(permissionId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(entity -> new Permission(entity.getId(), entity.getName()))
                .collect(Collectors.toList());
    }
}
