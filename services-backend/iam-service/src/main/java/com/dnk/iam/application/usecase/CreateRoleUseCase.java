package com.dnk.iam.application.usecase;

import com.dnk.iam.application.port.out.RoleRepositoryPort;
import com.dnk.iam.domain.model.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateRoleUseCase {

    private final RoleRepositoryPort roleRepository;

    @Transactional
    public Role execute(String roleName) {
        if (roleName == null || roleName.trim().isEmpty()) {
            throw new IllegalArgumentException("Role name cannot be empty");
        }
        
        log.info("Creating new role: {}", roleName);
        Role role = new Role(UUID.randomUUID(), roleName.trim());
        return roleRepository.save(role);
    }
}
