package com.dnk.iam.application.port.out;

import com.dnk.iam.domain.model.Permission;

import java.util.List;
import java.util.Optional;

public interface PermissionRepositoryPort {
    
    Permission save(Permission permission);
    
    Optional<Permission> findByName(String name);
    
    List<Permission> findAll();
}
