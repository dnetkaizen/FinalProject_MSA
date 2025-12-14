package com.dnk.enrollment.infrastructure.security;

import com.dnk.enrollment.domain.model.AuthenticatedUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

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
                
                // Create a UserDetails object with the authenticated user information
                UserDetails userDetails = new User(
                        authenticatedUser.userId(),
                        "", // No password needed as we're using JWT
                        Collections.emptyList() // No authorities/roles for now
                );
                
                // Create an authentication token with AuthenticatedUser as principal for @AuthenticationPrincipal
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        authenticatedUser, null, userDetails.getAuthorities());
                
                // Set the authentication in the security context
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
            } catch (SecurityException e) {
                // Log the error and continue the filter chain
                logger.warn("JWT validation failed: " + e.getMessage());
                // The request will be rejected by the security configuration
            }
        }

        // Continue with the filter chain
        chain.doFilter(request, response);
    }
}

