package com.sanu.anonymouschat.service;

import com.sanu.anonymouschat.dto.AddBoyRequest;
import com.sanu.anonymouschat.dto.FullSignupRequest;
import com.sanu.anonymouschat.dto.SignupRequest;
import com.sanu.anonymouschat.entity.User;
import com.sanu.anonymouschat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(FullSignupRequest request) {
        // Check for username uniqueness (for login)
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username '" + request.getUsername() + "' is already taken.");
        }

        // Check for email uniqueness
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty() && userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email '" + request.getEmail() + "' is already registered.");
        }

        User user = new User();
        user.setUsername(request.getUsername()); // Keep the original username for login
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Assign the generated anonymous display name
        user.setDisplayName(generateUniqueAnonymousName());

        String genderFromForm = request.getGender().toLowerCase();
        boolean requiresApproval = false;

        if ("female".equals(genderFromForm)) {
            user.setGender(User.Gender.FEMALE);
            user.setRole(User.Role.ROLE_GIRL);
            user.setApproved(true);
        } else if ("male".equals(genderFromForm)) {
            user.setGender(User.Gender.MALE);
            user.setRole(User.Role.ROLE_BOY);
            user.setApproved(false);
            requiresApproval = true;
        } else {
            throw new IllegalArgumentException("Invalid gender specified: " + request.getGender());
        }

        user.setOnline(false);

        System.out.println("Attempting to register new user: " + user.getUsername() +
                ", DisplayName: " + user.getDisplayName() +
                ", Gender: " + user.getGender().name() +
                ", Role: " + user.getRole().name() +
                ", Approved: " + user.isApproved());

        User savedUser = userRepository.save(user);

        if (requiresApproval) {
            System.out.println("Successfully registered user: " + savedUser.getUsername() +
                    " with DisplayName: " + savedUser.getDisplayName() +
                    " and ID: " + savedUser.getId() + ". Awaiting admin approval.");
        } else {
            System.out.println("Successfully registered user: " + savedUser.getUsername() +
                    " with DisplayName: " + savedUser.getDisplayName() +
                    " and ID: " + savedUser.getId() + ".");
        }
        return savedUser;
    }

    // This method generates a UNIQUE anonymous display name by checking against the `displayName` field in the database.
    private String generateUniqueAnonymousName() {
        String anonymousName;
        Random random = new Random();
        do {
            anonymousName = "User#" + (1000 + random.nextInt(9000));
        } while (userRepository.findByDisplayName(anonymousName).isPresent());
        return anonymousName;
    }

    @Transactional
    public User registerGirl(SignupRequest request) {
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty() && userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email '" + request.getEmail() + "' is already registered.");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setGender(User.Gender.FEMALE);
        user.setRole(User.Role.ROLE_GIRL);
        user.setApproved(true);
        user.setDisplayName(generateUniqueAnonymousName());
        // Decide: is this method for an API that also uses anonymous login names?
        // If so, `username` might also be anonymous. Otherwise, ensure it's handled.
        // For consistency, let's make it anonymous if the idea is "signup a girl with anonymous ID".
        user.setUsername(generateUniqueAnonymousName()); // Assign anonymous username for API login

        System.out.println("Attempting to register new girl (API) user: " + user.getUsername() + " (Display: " + user.getDisplayName() + ") with email: " + user.getEmail());
        User savedUser = userRepository.save(user);
        System.out.println("Successfully registered girl (API) user: " + savedUser.getUsername() + " (Display: " + savedUser.getDisplayName() + ") with ID: " + savedUser.getId());
        return savedUser;
    }

    @Transactional
    public User addBoyByAdmin(AddBoyRequest request) {
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty() && userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email '" + request.getEmail() + "' is already registered.");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setGender(User.Gender.MALE);
        user.setRole(User.Role.ROLE_BOY);
        user.setApproved(true);
        user.setDisplayName(generateUniqueAnonymousName());
        user.setUsername(generateUniqueAnonymousName()); // Assign anonymous username for admin-added boys

        System.out.println("Attempting to add new boy user via admin: " + user.getUsername() + " (Display: " + user.getDisplayName() + ") with email: " + user.getEmail());
        User savedUser = userRepository.save(user);
        System.out.println("Successfully added boy user: " + savedUser.getUsername() + " (Display: " + savedUser.getDisplayName() + ") with ID: " + savedUser.getId());
        return savedUser;
    }

    /**
     * Finds a User by their unique display name.
     * @param displayName The anonymous display name of the user.
     * @return An Optional containing the User if found, empty otherwise.
     */
    public Optional<User> findByDisplayName(String displayName) {
        return userRepository.findByDisplayName(displayName);
    }

    /**
     * Finds a User by their original login username.
     * @param username The original login username of the user.
     * @return An Optional containing the User if found, empty otherwise.
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> getPendingApprovalUsers() {
        System.out.println("UserService: Fetching users awaiting approval...");
        List<User> pendingUsers = userRepository.findByApprovedFalse();
        System.out.println("UserService: Found " + pendingUsers.size() + " users awaiting approval.");
        return pendingUsers;
    }

    @Transactional
    public void approveUser(Long userId) {
        System.out.println("UserService: Attempting to approve user with ID: " + userId);
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (!user.isApproved()) {
                user.setApproved(true);
                userRepository.save(user);
                System.out.println("UserService: User '" + user.getUsername() + "' (Display: " + user.getDisplayName() + ") (ID: " + userId + ") successfully approved.");
            } else {
                System.out.println("UserService: User '" + user.getUsername() + "' (Display: " + user.getDisplayName() + ") (ID: " + userId + ") was already approved.");
            }
        } else {
            throw new IllegalArgumentException("User with ID " + userId + " not found for approval.");
        }
    }

    @Transactional
    public void updateOnlineStatus(String username, boolean online) {
        System.out.println("UserService: Attempting to update online status for user: " + username + " to " + online);
        userRepository.findByUsername(username).ifPresentOrElse(user -> {
            user.setOnline(online);
            userRepository.save(user);
            userRepository.flush();
            System.out.println("UserService: User online status updated to " + online + " for: " + username);
        }, () -> {
            System.out.println("UserService: User not found for online status update: " + username);
        });
    }

    public List<User> getOppositeGenderOnlineUsers(String currentUsername) {
        System.out.println("UserService: getOppositeGenderOnlineUsers called for current user: " + currentUsername);

        Optional<User> currentUserOptional = userRepository.findByUsername(currentUsername);

        if (currentUserOptional.isEmpty()) {
            System.out.println("UserService: Current user '" + currentUsername + "' not found.");
            return List.of();
        }

        User currentUser = currentUserOptional.get();
        User.Gender currentGender = currentUser.getGender();
        User.Gender oppositeGender = (currentGender == User.Gender.MALE) ? User.Gender.FEMALE : User.Gender.MALE;

        System.out.println("UserService: Current user's gender: " + currentGender.name() + ". Looking for opposite gender: " + oppositeGender.name());

        List<User> oppositeUsers = userRepository.findByGenderAndOnline(oppositeGender, true);

        System.out.println("UserService: Found " + oppositeUsers.size() + " online users of opposite gender.");
        oppositeUsers.forEach(user -> System.out.println("UserService: Found user: " + user.getUsername() +
                " (Display: " + user.getDisplayName() + "), Gender: " + user.getGender().name() +
                ", Online: " + user.isOnline()));

        return oppositeUsers;
    }
}