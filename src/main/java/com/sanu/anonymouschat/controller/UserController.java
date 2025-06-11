package com.sanu.anonymouschat.controller;

import com.sanu.anonymouschat.dto.AddBoyRequest;
import com.sanu.anonymouschat.entity.User;
import com.sanu.anonymouschat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity; // Import ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
// import java.util.Map; // Remove this if you no longer return Map

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    // Signup for boys - only accessible by ADMIN role
    @PostMapping("/signup/boy")
    @PreAuthorize("hasRole('ADMIN')")
    public User signupBoy(@RequestBody AddBoyRequest request) {
        return userService.addBoyByAdmin(request);
    }

    // Update online status for logged-in user
    @PostMapping("/online-status")
    public String updateStatus(@RequestParam boolean online, Principal principal) {
        if (principal == null) return "Unauthorized";
        userService.updateOnlineStatus(principal.getName(), online);
        return "Online status updated to: " + online;
    }

    // Get list of online users of opposite gender
    @GetMapping("/opposite-online")
    public List<User> getOppositeOnlineUsers(Principal principal) {
        if (principal == null) {
            return List.of();
        }
        return userService.getOppositeGenderOnlineUsers(principal.getName());
    }

    /**
     * Retrieves the current authenticated user's details, including their username and display name.
     * @param principal The authenticated user's principal.
     * @return ResponseEntity containing the User object if found, or 404 Not Found.
     */
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(Principal principal) { // Change return type to ResponseEntity<User>
        if (principal == null) {
            // Return 404 Not Found if no principal (user not authenticated)
            return ResponseEntity.notFound().build();
        }
        // Fetch the full User object from the UserService using the principal's username
        return userService.findByUsername(principal.getName())
                .map(ResponseEntity::ok) // If user is found, return 200 OK with the User object
                .orElse(ResponseEntity.notFound().build()); // If not found (shouldn't happen for authenticated), return 404
    }
}