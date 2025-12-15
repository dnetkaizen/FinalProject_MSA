package com.dnk.iam.infrastructure.persistence.repository;

import com.dnk.iam.application.port.out.PermissionRepositoryPort;
import com.dnk.iam.domain.model.Permission;
import com.dnk.iam.infrastructure.persistence.entity.PermissionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PermissionRepositoryAdapter implements PermissionRepositoryPort {

    private final PermissionJpaRepository permissionJpaRepository;

    @Override
    public Permission save(Permission permission) {
        PermissionEntity entity = PermissionEntity.builder()
                .id(permission.id())
                .name(permission.name())
                .build();
        PermissionEntity saved = permissionJpaRepository.save(entity);
        return new Permission(saved.getId(), saved.getName());
    }

    @Override
    public Optional<Permission> findByName(String name) {
        return permissionJpaRepository.findByName(name)
                .map(entity -> new Permission(entity.getId(), entity.getName()));
    }

    @Override
    public List<Permission> findAll() {
        return permissionJpaRepository.findAll().stream()
                .map(entity -> new Permission(entity.getId(), entity.getName()))
                .collect(Collectors.toList());
    }
}
