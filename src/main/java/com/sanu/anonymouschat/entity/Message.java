package com.sanu.anonymouschat.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages") // Ensure your table name is 'messages'
@Getter // Lombok annotation to generate getters
@Setter // Lombok annotation to generate setters
@NoArgsConstructor // Lombok annotation to generate no-argument constructor
@AllArgsConstructor // Lombok annotation to generate constructor with all arguments
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Use ManyToOne relationship to the User entity for sender
    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false) // Foreign key column in 'messages' table
    private User sender;

    // Use ManyToOne relationship to the User entity for receiver
    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false) // Foreign key column in 'messages' table
    private User receiver;

    @Column(nullable = false, columnDefinition = "TEXT") // Content can be long text
    private String content;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private boolean readStatus = false; // NEW FIELD: Default to false (unread)
}