package com.dnk.iam.application.port.out;

import com.dnk.iam.domain.model.Role;

import java.util.List;
import java.util.UUID;

public interface UserRoleRepositoryPort {
    
    void assignRole(String userId, UUID roleId);
    
    List<Role> findRolesByUserId(String userId);
}
