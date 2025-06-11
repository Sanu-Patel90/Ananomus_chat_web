package com.sanu.anonymouschat.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class SignupRequest {
    private String email;
    private String password;
    // no gender field needed here since it's fixed for girls signup
}
