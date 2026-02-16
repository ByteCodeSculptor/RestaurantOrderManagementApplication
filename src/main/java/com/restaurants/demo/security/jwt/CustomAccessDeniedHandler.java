package com.restaurants.demo.security.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/*
    CustomAccessDeniedHandler is a custom implementation of AccessDeniedHandler that handles access denied exceptions.
    When an authenticated user tries to access a resource they don't have permission for, this class sends a JSON response with a 403 status code and an error message.
 */

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException ex)
            throws IOException {

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        response.getWriter().write("""
            {
              "status": 403,
              "message": "Access Denied - You are not authorized"
            }
        """);
    }
}

