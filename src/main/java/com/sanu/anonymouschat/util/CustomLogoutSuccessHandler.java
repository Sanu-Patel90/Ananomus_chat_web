package com.sanu.anonymouschat.util;

import com.sanu.anonymouschat.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private final UserService userService;

    public CustomLogoutSuccessHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            System.out.println("CustomLogoutSuccessHandler: User '" + username + "' is logging out. Updating online status to false.");
            userService.updateOnlineStatus(username, false);
        } else {
            System.out.println("CustomLogoutSuccessHandler: Anonymous or unauthenticated user performing logout.");
        }

        // Redirect to the login page with a logout success parameter
        response.sendRedirect("/auth/login?logout=true"); // This is the key redirect
    }
}