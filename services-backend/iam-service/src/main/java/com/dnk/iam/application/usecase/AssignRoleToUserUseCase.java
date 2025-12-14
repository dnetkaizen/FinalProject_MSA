package com.dnk.iam.application.usecase;

import com.dnk.iam.application.port.out.RoleRepositoryPort;
import com.dnk.iam.application.port.out.UserRoleRepositoryPort;
import com.dnk.iam.domain.model.Role;
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

        String trimmedUserId = userId.trim();
        String trimmedRoleName = roleName.trim();
        
        log.info("AUDIT: Assigning role to user - userId: {}, roleName: {}", trimmedUserId, trimmedRoleName);
        Role role = roleRepository.findByName(trimmedRoleName)
                .orElseThrow(() -> new EntityNotFoundException("Role not found: " + trimmedRoleName));
        
        userRoleRepository.assignRole(trimmedUserId, role.id());
        log.info("AUDIT: Role assigned to user successfully - userId: {}, roleName: {}", trimmedUserId, trimmedRoleName);
    }
}
