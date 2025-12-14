package com.dnk.iam.application.usecase;

import com.dnk.iam.application.port.out.RoleRepositoryPort;
import com.dnk.iam.application.port.out.UserRoleRepositoryPort;
import com.dnk.iam.domain.model.Role;
import com.dnk.iam.domain.model.UserRole;
import com.dnk.iam.application.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssignRoleToUserUseCase {

    private final UserRoleRepositoryPort userRoleRepository;
    private final RoleRepositoryPort roleRepository;

    @Transactional
    public void execute(String userId, String roleName) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be empty");
        }
        if (roleName == null || roleName.trim().isEmpty()) {
            throw new IllegalArgumentException("Role name cannot be empty");
        }

        log.info("Assigning role '{}' to user '{}'", roleName, userId);
        Role role = roleRepository.findByName(roleName.trim())
                .orElseThrow(() -> new EntityNotFoundException("Role not found: " + roleName));
        
        userRoleRepository.assignRole(userId.trim(), role.id());
    }
}
