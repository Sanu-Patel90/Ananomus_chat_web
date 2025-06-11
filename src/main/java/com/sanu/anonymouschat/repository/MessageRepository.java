package com.sanu.anonymouschat.repository;

import com.sanu.anonymouschat.entity.Message;
import com.sanu.anonymouschat.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findBySenderAndReceiverOrReceiverAndSenderOrderByTimestampAsc(
            User sender1, User receiver1, User receiver2, User sender2);

    // NEW: Count unread messages for a specific receiver, grouped by sender's display name
    @Query("SELECT m.sender.displayName, COUNT(m) FROM Message m WHERE m.receiver = :receiver AND m.readStatus = FALSE GROUP BY m.sender.displayName")
    List<Object[]> countUnreadMessagesBySender(@Param("receiver") User receiver);

    // NEW: Mark messages as read for a specific conversation (only incoming messages for the currentUser)
    @Modifying
    @Query("UPDATE Message m SET m.readStatus = TRUE WHERE (m.sender = :otherUser AND m.receiver = :currentUser AND m.readStatus = FALSE)")
    void markIncomingMessagesAsRead(@Param("currentUser") User currentUser, @Param("otherUser") User otherUser);
}