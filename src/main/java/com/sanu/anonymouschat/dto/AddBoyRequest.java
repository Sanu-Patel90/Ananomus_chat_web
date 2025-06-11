package com.sanu.anonymouschat.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class AddBoyRequest {
    private String email;
    private String password;
}
