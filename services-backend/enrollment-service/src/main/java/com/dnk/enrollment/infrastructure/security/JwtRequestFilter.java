package com.dnk.enrollment.infrastructure.security;

import com.dnk.enrollment.application.port.out.IamServicePort;
import com.dnk.enrollment.domain.model.AuthenticatedUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtValidator jwtValidator;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String requestTokenHeader = request.getHeader("Authorization");

        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            try {
                // Validate the token and get the authenticated user
                AuthenticatedUser authenticatedUser = jwtValidator.validateAndExtractUser(requestTokenHeader);
                String userId = authenticatedUser.userId();

                // Build authorities from Token Claims (Stateless)
                java.util.List<SimpleGrantedAuthority> authorities = new java.util.ArrayList<>();
                
                // Map Roles to ROLE_xxx
                if (authenticatedUser.roles() != null) {
                    authenticatedUser.roles().forEach(role -> 
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + role))
                    );
                }

                // Map Permissions as is
                if (authenticatedUser.permissions() != null) {
                    authenticatedUser.permissions().forEach(perm -> 
                        authorities.add(new SimpleGrantedAuthority(perm))
                    );
                }

                log.debug("User {} authorities: {}", userId, authorities);

                // Create authentication with authorities
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        authenticatedUser, null, authorities);

                // Set the authentication in the security context
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                // Log the error and continue the filter chain
                log.warn("JWT validation failed: " + e.getMessage());
                // The request will be rejected by the security configuration if auth is missing
            }
        }

        // Continue with the filter chain
        chain.doFilter(request, response);
    }
}
