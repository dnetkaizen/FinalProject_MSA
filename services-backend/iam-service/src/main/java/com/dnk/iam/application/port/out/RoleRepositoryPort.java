package com.dnk.iam.application.port.out;

import com.dnk.iam.domain.model.Role;

import java.util.List;
import java.util.Optional;

public interface RoleRepositoryPort {
    
    Role save(Role role);
    
    Optional<Role> findByName(String name);
    
    List<Role> findAll();
}
