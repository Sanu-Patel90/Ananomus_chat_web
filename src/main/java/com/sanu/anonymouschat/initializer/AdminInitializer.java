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

            // --- IMPORTANT FIX: Assign Enum values directly ---
            admin.setGender(User.Gender.MALE); // Assign the MALE enum constant
            admin.setRole(User.Role.ROLE_ADMIN); // Assign the ROLE_ADMIN enum constant
            // --- END IMPORTANT FIX ---

            admin.setApproved(true); // Admin user should always be approved
            admin.setOnline(false); // Admin user starts offline

            userRepository.save(admin);
            System.out.println("Admin user '" + adminUsername + "' created successfully.");
        } else {
            System.out.println("Admin user '" + adminUsername + "' already exists. Skipping creation.");
        }
    }
}