package com.sanu.anonymouschat.controller;

import com.sanu.anonymouschat.service.UserService; // Still needed if CustomLoginSuccessHandler is used in SecurityConfig
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller responsible for handling authentication-related requests,
 * specifically displaying the login page.
 */
@Controller
@RequestMapping("/auth") // All mappings in this controller will start with /auth
public class AuthController {

    // UserService is typically injected here because Spring Security's
    // CustomLoginSuccessHandler might use it, and AuthController might
    // historically have had other authentication-related API methods.
    // Even if not directly used by this specific @GetMapping, it's common to keep it.
    @Autowired
    private UserService userService;

    /**
     * Handles requests to display the login page.
     * This method maps to the URL /auth/login.
     *
     * @return The logical view name "auth/login", which Spring Boot
     * will resolve to src/main/resources/templates/auth/login.html.
     */
    @GetMapping("/login")
    public String showLoginPage() {
        return "auth/login"; // Refers to src/main/resources/templates/auth/login.html
    }

    // IMPORTANT: All registration-related GET and POST methods
    // have been removed from this controller to avoid ambiguous mapping errors.
    // Registration functionality is now handled exclusively in PageController.
}