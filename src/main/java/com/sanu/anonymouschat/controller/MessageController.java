package com.sanu.anonymouschat.controller;

import com.sanu.anonymouschat.dto.ChatMessageRequest;
import com.sanu.anonymouschat.dto.MessageDTO;
import com.sanu.anonymouschat.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat")
public class MessageController {

    @Autowired
    private MessageService messageService;

    // This controller no longer directly uses UserService for display name lookups
    // as MessageService methods are designed to handle User objects internally.

    @PostMapping("/send")
    public ResponseEntity<MessageDTO> sendMessage(@RequestBody ChatMessageRequest request, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build(); // Unauthorized
        }
        // MessageService now expects ChatMessageRequest and handles finding receiver by displayName
        // It returns a MessageDTO that includes sender/receiver display names and read status.
        MessageDTO sentMessage = messageService.sendMessage(principal.getName(), request);
        return ResponseEntity.ok(sentMessage);
    }

    @GetMapping("/conversation/{otherUserDisplayName}")
    public ResponseEntity<List<MessageDTO>> getConversation(@PathVariable String otherUserDisplayName, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build(); // Unauthorized
        }
        // MessageService now directly returns List<MessageDTO> and handles marking messages as read
        List<MessageDTO> messages = messageService.getConversation(principal.getName(), otherUserDisplayName);
        return ResponseEntity.ok(messages);
    }

    /**
     * NEW: Endpoint to get unread message counts for the current authenticated user.
     * Returns a map where keys are sender display names and values are their unread message counts.
     */
    @GetMapping("/unread-counts")
    public ResponseEntity<Map<String, Long>> getUnreadCounts(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build(); // Unauthorized
        }
        Map<String, Long> unreadCounts = messageService.getUnreadCounts(principal.getName());
        return ResponseEntity.ok(unreadCounts);
    }

    /**
     * NEW: Endpoint to mark all incoming messages from a specific sender as read.
     * This is typically called when the current user opens a chat conversation with the other user.
     * @param otherUserDisplayName The display name of the user whose messages should be marked as read.
     * @param principal The authenticated user's principal.
     * @return ResponseEntity indicating success.
     */
    @PostMapping("/mark-as-read/{otherUserDisplayName}")
    public ResponseEntity<Void> markMessagesAsRead(@PathVariable String otherUserDisplayName, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build(); // Unauthorized
        }
        // MessageService now specifically marks INCOMING messages from otherUserDisplayName as read
        messageService.markIncomingMessagesAsRead(principal.getName(), otherUserDisplayName);
        return ResponseEntity.ok().build();
    }

    // Removed the old /testSend endpoint for clarity and consistency with new DTOs.
    // If you need a test endpoint, consider recreating it using ChatMessageRequest DTO.
}