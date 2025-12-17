package com.dnk.enrollment.infrastructure.adapter.out;

import com.dnk.enrollment.application.port.out.IamServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class IamServiceAdapter implements IamServicePort {

    @Override
    public List<String> getUserRoles(String userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.getName().equals(userId)) {
            log.warn("Cannot fetch roles for different user from SecurityContext. Req: {}, Auth: {}", userId, auth != null ? auth.getName() : "null");
            return Collections.emptyList();
        }

        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> a.substring(5)) // Remove ROLE_ prefix
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getUserPermissions(String userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.getName().equals(userId)) {
            return Collections.emptyList();
        }

        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> !a.startsWith("ROLE_"))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAdmin(String userId) {
        return getUserRoles(userId).contains("admin");
    }

    @Override
    public boolean hasPermission(String userId, String permission) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.getName().equals(userId)) {
            return false;
        }
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(permission));
    }
}
