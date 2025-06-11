package com.sanu.anonymouschat.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// This DTO is for incoming chat messages from the frontend.
// The frontend now sends the receiver's DISPLAY NAME.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageRequest {
    private String receiverDisplayName; // Changed from receiverUsername
    private String content;
    // You might add more fields later, like a timestamp from the client side if needed.
}