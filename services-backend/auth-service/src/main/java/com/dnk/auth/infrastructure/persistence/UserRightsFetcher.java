package com.dnk.auth.infrastructure.persistence;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.List;

@Component
public class UserRightsFetcher {

    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<String> getUserRoles(String userId) {
        String sql = "SELECT r.name FROM iam_schema.roles r " +
                     "JOIN iam_schema.user_roles ur ON r.id = ur.role_id " +
                     "WHERE ur.user_id = :userId";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<String> getUserPermissions(String userId) {
        String sql = "SELECT DISTINCT p.name FROM iam_schema.permissions p " +
                     "JOIN iam_schema.role_permissions rp ON p.id = rp.permission_id " +
                     "JOIN iam_schema.user_roles ur ON rp.role_id = ur.role_id " +
                     "WHERE ur.user_id = :userId";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    @Transactional
    public void assignRole(String userId, String roleName) {
        String sql = "INSERT INTO iam_schema.user_roles (user_id, role_id) " +
                     "SELECT :userId, r.id FROM iam_schema.roles r WHERE r.name = :roleName " +
                     "ON CONFLICT DO NOTHING";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("userId", userId);
        query.setParameter("roleName", roleName);
        query.executeUpdate();
    }
}
