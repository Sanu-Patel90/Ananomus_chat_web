package com.sanu.anonymouschat.util;

import com.sanu.anonymouschat.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

// Note: This handler performs actions *before* the session is invalidated.
// Online status update is now primarily handled by CustomLogoutSuccessHandler.
@Component
public class CustomLogoutHandler implements LogoutHandler {

    private final UserService userService; // Keeping for potential future pre-logout logic

    public CustomLogoutHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // We are NOT updating online status here to avoid duplication.
        // CustomLogoutSuccessHandler will handle setting online status to false.

        System.out.println("CustomLogoutHandler: Performing pre-logout actions for user: " + (authentication != null ? authentication.getName() : "anonymous"));
        // Add any other pre-logout cleanup here if necessary,
        // for example, invalidating specific tokens that must be invalidated immediately
        // before the security context is fully cleared or session invalidated.
    }
}