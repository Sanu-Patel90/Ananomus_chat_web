package com.sanu.anonymouschat.dto; // Or wherever your DTOs are located

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class FullSignupRequest {
    private String username; // Comes directly from the form
    private String email;
    private String password;
    private String gender;   // Comes directly from the form (GIRL/BOY)
}