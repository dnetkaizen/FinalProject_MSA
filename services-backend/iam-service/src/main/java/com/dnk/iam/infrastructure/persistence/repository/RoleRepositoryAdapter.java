package com.dnk.iam.infrastructure.persistence.repository;

import com.dnk.iam.application.port.out.RoleRepositoryPort;
import com.dnk.iam.domain.model.Role;
import com.dnk.iam.infrastructure.persistence.entity.RoleEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class RoleRepositoryAdapter implements RoleRepositoryPort {

    private final RoleJpaRepository roleJpaRepository;

    @Override
    public Role save(Role role) {
        RoleEntity entity = RoleEntity.builder()
                .id(role.id())
                .name(role.name())
                .build();
        RoleEntity saved = roleJpaRepository.save(entity);
        return new Role(saved.getId(), saved.getName());
    }

    @Override
    public Optional<Role> findByName(String name) {
        return roleJpaRepository.findByName(name)
                .map(entity -> new Role(entity.getId(), entity.getName()));
    }

    @Override
    public List<Role> findAll() {
        return roleJpaRepository.findAll().stream()
                .map(entity -> new Role(entity.getId(), entity.getName()))
                .collect(Collectors.toList());
    }
}
