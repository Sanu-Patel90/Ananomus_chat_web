// src/main/java/com/sanu/anonymouschat/controller/AdminController.java
package com.sanu.anonymouschat.controller;

import com.sanu.anonymouschat.entity.User;
import com.sanu.anonymouschat.entity.User;
import com.sanu.anonymouschat.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')") // Only users with ROLE_ADMIN can access this controller
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/pending-users")
    public String showPendingUsers(Model model) {
        List<User> pendingUsers = userService.getPendingApprovalUsers();
        model.addAttribute("pendingUsers", pendingUsers);
        return "admin/pending-users"; // This will map to src/main/resources/templates/admin/pending-users.html
    }

    @PostMapping("/approve-user/{userId}")
    public String approveUser(@PathVariable Long userId, RedirectAttributes redirectAttributes) {
        try {
            userService.approveUser(userId);
            redirectAttributes.addFlashAttribute("successMessage", "User approved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error approving user: " + e.getMessage());
        }
        return "redirect:/admin/pending-users";
    }
}