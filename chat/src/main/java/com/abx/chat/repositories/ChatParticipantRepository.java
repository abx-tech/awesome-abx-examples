package com.abx.chat.repositories;

import com.abx.chat.model.ChatParticipant;
import com.abx.chat.model.ChatThread;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, Long> {

    // Find all chat threads a user is a part of
    @Query("SELECT cp.chatThread FROM ChatParticipant cp WHERE cp.user.id = :userId")
    List<ChatThread> findChatThreadsByUserId(@Param("userId") UUID userId);

    List<ChatParticipant> findByUserId(UUID userId);

}

