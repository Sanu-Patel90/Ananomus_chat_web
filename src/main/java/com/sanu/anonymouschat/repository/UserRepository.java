package com.sanu.anonymouschat.repository;

import com.sanu.anonymouschat.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByDisplayName(String displayName); // NEW: For checking unique display names
    List<User> findByApprovedFalse();
    List<User> findByGenderAndOnline(User.Gender gender, boolean online);
    // Add other methods as needed
}