package com.dnk.enrollment.infrastructure.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, 
                        HttpServletResponse response, 
                        AuthenticationException authException) throws IOException {
        
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        
        String message = "Unauthorized: Authentication token was either missing or invalid";
        
        // If there's a more specific message from the exception, use it
        if (authException != null && authException.getMessage() != null) {
            message = authException.getMessage();
        }
        
        String jsonResponse = String.format(
            "{\"status\": %d, \"error\": \"%s\", \"message\": \"%s\"}",
            HttpServletResponse.SC_UNAUTHORIZED,
            "Unauthorized",
            message
        );
        
        response.getWriter().write(jsonResponse);
    }
}

