package com.dnk.iam.infrastructure.persistence.repository;

import com.dnk.iam.application.port.out.UserRoleRepositoryPort;
import com.dnk.iam.domain.model.Role;
import com.dnk.iam.infrastructure.persistence.entity.UserRoleEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserRoleRepositoryAdapter implements UserRoleRepositoryPort {

    private final UserRoleJpaRepository userRoleJpaRepository;
    private final RoleJpaRepository roleJpaRepository;

    @Override
    public void assignRole(String userId, UUID roleId) {
        UserRoleEntity.UserRoleId id = new UserRoleEntity.UserRoleId(userId, roleId);
        UserRoleEntity entity = new UserRoleEntity();
        entity.setId(id);
        userRoleJpaRepository.save(entity);
    }

    @Override
    public List<Role> findRolesByUserId(String userId) {
        return userRoleJpaRepository.findByUserId(userId).stream()
                .map(entity -> entity.getId().getRoleId())
                .map(roleId -> roleJpaRepository.findById(roleId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(entity -> new Role(entity.getId(), entity.getName()))
                .collect(Collectors.toList());
    }
}
