package com.dnk.enrollment.application.port.out;

import java.util.List;

public interface IamServicePort {
    List<String> getUserRoles(String userId);

    List<String> getUserPermissions(String userId);

    boolean isAdmin(String userId);

    boolean hasPermission(String userId, String permission);
}
