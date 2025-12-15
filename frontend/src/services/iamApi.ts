import http from './http';

// Types
export interface Role {
    id: string;
    name: string;
    description?: string;
}

export interface Permission {
    id: string;
    name: string;
    resource: string;
    action: string;
}

export interface UserRole {
    userId: string;
    roleId: string;
    assignedAt: string;
    role: Role;
}

export interface RolePermission {
    roleId: string;
    permissionId: string;
    permission: Permission;
}

export interface CreateRoleRequest {
    name: string;
    description?: string;
}

export interface AssignRoleRequest {
    userId: string;
    roleId: string;
}

export interface AssignPermissionRequest {
    roleId: string;
    permissionId: string;
}

/**
 * IAM (Identity and Access Management) API service
 */
export const iamApi = {
    /**
     * Get all roles
     */
    async getRoles(): Promise<Role[]> {
        const response = await http.iam.get<Role[]>('/iam/roles');
        return response.data;
    },

    /**
     * Get role by ID
     */
    async getRoleById(roleId: string): Promise<Role> {
        const response = await http.iam.get<Role>(`/iam/roles/${roleId}`);
        return response.data;
    },

    /**
     * Create a new role
     */
    async createRole(data: CreateRoleRequest): Promise<Role> {
        const response = await http.iam.post<Role>('/iam/roles', data);
        return response.data;
    },

    /**
     * Update a role
     */
    async updateRole(roleId: string, data: CreateRoleRequest): Promise<Role> {
        const response = await http.iam.put<Role>(`/iam/roles/${roleId}`, data);
        return response.data;
    },

    /**
     * Delete a role
     */
    async deleteRole(roleId: string): Promise<void> {
        await http.iam.delete(`/iam/roles/${roleId}`);
    },

    /**
     * Get all permissions
     */
    async getPermissions(): Promise<Permission[]> {
        const response = await http.iam.get<Permission[]>('/iam/permissions');
        return response.data;
    },

    /**
     * Get user roles
     */
    async getUserRoles(userId: string): Promise<UserRole[]> {
        const response = await http.iam.get<UserRole[]>(`/iam/users/${userId}/roles`);
        return response.data;
    },

    /**
     * Assign role to user
     */
    async assignRoleToUser(data: AssignRoleRequest): Promise<UserRole> {
        const response = await http.iam.post<UserRole>('/iam/user-roles', data);
        return response.data;
    },

    /**
     * Remove role from user
     */
    async removeRoleFromUser(userId: string, roleId: string): Promise<void> {
        await http.iam.delete(`/iam/users/${userId}/roles/${roleId}`);
    },

    /**
     * Get role permissions
     */
    async getRolePermissions(roleId: string): Promise<RolePermission[]> {
        const response = await http.iam.get<RolePermission[]>(`/iam/roles/${roleId}/permissions`);
        return response.data;
    },

    /**
     * Assign permission to role
     */
    async assignPermissionToRole(data: AssignPermissionRequest): Promise<RolePermission> {
        const response = await http.iam.post<RolePermission>('/iam/role-permissions', data);
        return response.data;
    },

    /**
     * Remove permission from role
     */
    async removePermissionFromRole(roleId: string, permissionId: string): Promise<void> {
        await http.iam.delete(`/iam/roles/${roleId}/permissions/${permissionId}`);
    },

    /**
     * Check if user has permission
     */
    async checkUserPermission(userId: string, resource: string, action: string): Promise<boolean> {
        const response = await http.iam.get<{ hasPermission: boolean }>(
            `/iam/users/${userId}/permissions/check`,
            {
                params: { resource, action }
            }
        );
        return response.data.hasPermission;
    },
};
