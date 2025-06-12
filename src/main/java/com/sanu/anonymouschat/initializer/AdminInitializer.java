package com.sanu.anonymouschat.initializer;

import com.sanu.anonymouschat.entity.User;
import com.sanu.anonymouschat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        String adminEmail = "admin@example.com";
        String adminUsername = "admin";
        String adminPassword = "Admin@123";

        // Check if the admin user already exists by username
        // Using findByUsername().isPresent() is efficient.
        boolean exists = userRepository.findByUsername(adminUsername).isPresent();

        if (!exists) {
            User admin = new User();
            admin.setEmail(adminEmail);
            admin.setUsername(adminUsername);
            admin.setPassword(passwordEncoder.encode(adminPassword));

            admin.setGender(User.Gender.MALE);
            admin.setRole(User.Role.ROLE_ADMIN);

            // --- CRITICAL FIX: Add this line to set a display name for the admin user ---
            admin.setDisplayName("AdminUser"); // Or any other default display name you prefer
            // -------------------------------------------------------------------------

            admin.setApproved(true);
            admin.setOnline(false);

            userRepository.save(admin); // This is line 43 in your code, which caused the error
            System.out.println("Admin user '" + adminUsername + "' created successfully.");
        } else {
            System.out.println("Admin user '" + adminUsername + "' already exists. Skipping creation.");
        }
    }
}