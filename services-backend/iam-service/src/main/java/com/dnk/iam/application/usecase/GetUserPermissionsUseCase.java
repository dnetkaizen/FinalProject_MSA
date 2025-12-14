package com.dnk.iam.application.usecase;

import com.dnk.iam.application.port.out.RolePermissionRepositoryPort;
import com.dnk.iam.application.port.out.UserRoleRepositoryPort;
import com.dnk.iam.domain.model.Permission;
import com.dnk.iam.domain.model.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetUserPermissionsUseCase {

    private final UserRoleRepositoryPort userRoleRepository;
    private final RolePermissionRepositoryPort rolePermissionRepository;

    public List<String> execute(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be empty");
        }

        log.debug("Fetching permissions for user: {}", userId);
        List<Role> userRoles = userRoleRepository.findRolesByUserId(userId.trim());
        
        return userRoles.stream()
                .flatMap(role -> rolePermissionRepository.findPermissionsByRoleId(role.id()).stream())
                .map(Permission::name)
                .distinct()
                .collect(Collectors.toList());
    }
}
