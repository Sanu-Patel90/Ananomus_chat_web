package com.sanu.anonymouschat.service;

import com.sanu.anonymouschat.dto.ChatMessageRequest;
import com.sanu.anonymouschat.dto.MessageDTO;
import com.sanu.anonymouschat.entity.Message;
import com.sanu.anonymouschat.entity.User;
import com.sanu.anonymouschat.repository.MessageRepository;
import com.sanu.anonymouschat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import this

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional // This was already here, keep it.
    public MessageDTO sendMessage(String senderUsername, ChatMessageRequest request) {
        User sender = userRepository.findByUsername(senderUsername)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found: " + senderUsername));

        User receiver = userRepository.findByDisplayName(request.getReceiverDisplayName())
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found by display name: " + request.getReceiverDisplayName()));

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(request.getContent());
        message.setTimestamp(LocalDateTime.now());
        message.setReadStatus(false); // Mark as unread by default

        Message savedMessage = messageRepository.save(message);
        System.out.println("MessageService: Message sent from " + sender.getDisplayName() + " to " + receiver.getDisplayName() + ", content: " + savedMessage.getContent());

        return new MessageDTO(savedMessage.getSender().getUsername(),
                savedMessage.getSender().getDisplayName(),
                savedMessage.getReceiver().getDisplayName(),
                savedMessage.getContent(),
                savedMessage.getTimestamp(),
                savedMessage.isReadStatus());
    }

    @Transactional // ADD THIS ANNOTATION HERE!
    public List<MessageDTO> getConversation(String currentUsername, String otherUserDisplayName) {
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new IllegalArgumentException("Current user not found: " + currentUsername));

        User otherUser = userRepository.findByDisplayName(otherUserDisplayName)
                .orElseThrow(() -> new IllegalArgumentException("Other user not found by display name: " + otherUserDisplayName));

        List<Message> messages = messageRepository.findBySenderAndReceiverOrReceiverAndSenderOrderByTimestampAsc(
                currentUser, otherUser, currentUser, otherUser);

        System.out.println("MessageService: Retrieved conversation between " + currentUser.getDisplayName() + " and " + otherUser.getDisplayName() + ". Messages count: " + messages.size());

        // IMPORTANT: Mark only the messages RECEIVED BY the currentUser FROM the otherUser as read
        markIncomingMessagesAsRead(currentUser.getUsername(), otherUserDisplayName);

        return messages.stream()
                .map(msg -> new MessageDTO(msg.getSender().getUsername(),
                        msg.getSender().getDisplayName(),
                        msg.getReceiver().getDisplayName(),
                        msg.getContent(),
                        msg.getTimestamp(),
                        msg.isReadStatus()))
                .collect(Collectors.toList());
    }

    @Transactional // This was already here, keep it.
    public void markIncomingMessagesAsRead(String currentUsername, String otherUserDisplayName) {
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new IllegalArgumentException("Current user not found: " + currentUsername));
        User otherUser = userRepository.findByDisplayName(otherUserDisplayName)
                .orElseThrow(() -> new IllegalArgumentException("Other user not found by display name: " + otherUserDisplayName));

        // Only mark messages sent by the 'otherUser' to the 'currentUser' as read
        messageRepository.markIncomingMessagesAsRead(currentUser, otherUser);
        System.out.println("MessageService: Marked incoming messages from " + otherUser.getDisplayName() + " to " + currentUser.getDisplayName() + " as read.");
    }

    public Map<String, Long> getUnreadCounts(String currentUsername) {
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new IllegalArgumentException("Current user not found: " + currentUsername));

        List<Object[]> unreadCountsRaw = messageRepository.countUnreadMessagesBySender(currentUser);

        Map<String, Long> unreadCounts = new HashMap<>();
        for (Object[] row : unreadCountsRaw) {
            String senderDisplayName = (String) row[0];
            Long count = (Long) row[1];
            unreadCounts.put(senderDisplayName, count);
        }
        System.out.println("MessageService: Unread counts for " + currentUser.getDisplayName() + ": " + unreadCounts);
        return unreadCounts;
    }
}