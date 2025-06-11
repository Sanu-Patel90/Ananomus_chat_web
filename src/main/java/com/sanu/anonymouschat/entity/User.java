package com.sanu.anonymouschat.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor; // Add this if you want an all-args constructor

import java.util.Objects; // For equals and hashCode

@Entity
@Table(name = "users") // Assuming your table is named 'users'
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor // Optional, for convenience if you use it elsewhere
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username; // Original username for login

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private boolean approved = false; // For admin approval, especially for boys

    @Column(nullable = false)
    private boolean online = false;

    @Column(unique = true, nullable = false) // NEW FIELD: Anonymous display name
    private String displayName;

    // Enum for Gender
    public enum Gender {
        MALE, FEMALE
    }

    // Enum for Role
    public enum Role {
        ROLE_USER, ROLE_ADMIN, ROLE_BOY, ROLE_GIRL
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }
}