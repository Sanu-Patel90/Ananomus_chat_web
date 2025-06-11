package com.sanu.anonymouschat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private String senderUsername;
    private String senderDisplayName;
    private String receiverDisplayName;
    private String content;
    private LocalDateTime timestamp;
    private boolean readStatus; // NEW: Include read status
}