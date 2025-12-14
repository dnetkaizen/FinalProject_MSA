package com.dnk.iam.application.exception;

public class EntityNotFoundException extends RuntimeException {
    
    public EntityNotFoundException(String message) {
        super(message);
    }
    
    public static EntityNotFoundException roleNotFound(String name) {
        return new EntityNotFoundException("Role not found with name: " + name);
    }
    
    public static EntityNotFoundException permissionNotFound(String name) {
        return new EntityNotFoundException("Permission not found with name: " + name);
    }
}
