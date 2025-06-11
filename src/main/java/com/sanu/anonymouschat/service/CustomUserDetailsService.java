package com.sanu.anonymouschat.service;

import com.sanu.anonymouschat.entity.User;
import com.sanu.anonymouschat.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors; // ENSURE THIS IMPORT IS PRESENT!

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        if (!user.isApproved()) {
            throw new UsernameNotFoundException("Account not approved. Please wait for admin approval: " + username);
        }

        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()));

        // >>>>>> NEW DIAGNOSTIC LOGGING HERE <<<<<<
        String loadedRolesString = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(", "));
        System.out.println("CustomUserDetailsService: For user '" + username + "', loaded authorities: [" + loadedRolesString + "]");
        // >>>>>> END NEW DIAGNOSTIC LOGGING <<<<<<

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}