// src/main/java/com/sanu/anonymouschat/controller/DashboardController.java
package com.sanu.anonymouschat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping; // Import this if you use it

@Controller // This is a regular controller for views
public class DashboardController {

    @GetMapping("/user/online-page") // This directly maps to /user/online-page
    public String onlineUsersPage() {
        return "online-page"; // This will render src/main/resources/templates/online-page.html
    }

    // Optional: If you want a root home page after login, you could add:
    @GetMapping({"/", "/home"})
    public String homePage() {
        return "home"; // Assumes you have src/main/resources/templates/home.html
    }
}