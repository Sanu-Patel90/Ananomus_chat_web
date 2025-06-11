package com.sanu.anonymouschat.controller;

import com.sanu.anonymouschat.entity.User;
import com.sanu.anonymouschat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collection;
import java.util.Optional;

@Controller
public class PostLoginRedirectController {

    @Autowired
    private UserService userService;

    /**
     * This endpoint handles all successful logins and redirects users based on their roles.
     * It also updates the user's online status upon successful authentication.
     * @param authentication The authenticated user's details provided by Spring Security.
     * @return A redirect string to the appropriate dashboard.
     */
    @GetMapping("/postlogin-redirect")
    public String postLoginRedirect(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            System.out.println("PostLoginRedirectController: Unauthenticated access to /postlogin-redirect. Redirecting to login.");
            return "redirect:/auth/login"; // Should not happen with current SecurityConfig
        }

        String username = authentication.getName();
        System.out.println("PostLoginRedirectController: User " + username + " successfully authenticated. Determining redirect...");

        // Update user's online status now that they are logged in
        userService.updateOnlineStatus(username, true);
        System.out.println("PostLoginRedirectController: User " + username + " online status set to true.");

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // Log authorities for debugging
        String rolesString = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(java.util.stream.Collectors.joining(", "));
        System.out.println("PostLoginRedirectController: User " + username + " has authorities: [" + rolesString + "]");

        // Check for ADMIN role first
        boolean isAdmin = authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            System.out.println("PostLoginRedirectController: Redirecting ADMIN user: " + username + " to /admin/pending-users");
            return "redirect:/admin/pending-users";
        }

        // Check for BOY or GIRL roles
        boolean isBoy = authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_BOY"));
        boolean isGirl = authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_GIRL"));

        if (isBoy || isGirl) {
            System.out.println("PostLoginRedirectController: Redirecting chat user: " + username + " to /user/online-page");
            return "redirect:/user/online-page";
        }

        // Fallback for any other authenticated role not explicitly handled
        System.out.println("PostLoginRedirectController: Redirecting other authenticated user: " + username + " to /home (fallback)");
        return "redirect:/home";
    }
}