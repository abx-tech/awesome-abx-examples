package com.abx.chat.service;


import com.abx.chat.dto.ChatThreadDTO;
import com.abx.chat.model.ChatParticipant;
import com.abx.chat.model.ChatThread;
import com.abx.chat.repositories.ChatParticipantRepository;
import com.abx.chat.repositories.ChatThreadRepository;
import com.abx.chat.repositories.UserRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ChatThreadService {
    private final ChatThreadRepository chatThreadRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final UserRepository userRepository;

    public ChatThreadService(ChatThreadRepository chatThreadRepository,
        ChatParticipantRepository chatParticipantRepository, UserRepository userRepository) {
        this.chatThreadRepository = chatThreadRepository;
        this.chatParticipantRepository = chatParticipantRepository;
        this.userRepository = userRepository;
    }


    public List<ChatThreadDTO> getThreadsForUser(String userId) {
        List<ChatParticipant> participants = chatParticipantRepository.findByUserId(UUID.fromString(userId));
        return participants.stream()
            .map(participant -> ChatThreadDTO.fromEntity(participant.getChatThread()))
            .collect(Collectors.toList());
    }

    public ChatThreadDTO joinThread(String userId1, String userId2) {
        // Check if a thread already exists between these two users
        Optional<ChatThread> existingThread = findThreadBetweenUsers(userId1, userId2);
        if (existingThread.isPresent()) {
            return ChatThreadDTO.fromEntity(existingThread.get());
        }

        // If not, create a new thread and add both users as participants
        ChatThread newThread = createNewThread(userId1, userId2);
        return ChatThreadDTO.fromEntity(newThread);
    }

    public Optional<ChatThread> findThreadBetweenUsers(String userId1, String userId2) {
        List<ChatThread> user1Threads = chatParticipantRepository
            .findChatThreadsByUserId(UUID.fromString(userId1));
        List<ChatThread> user2Threads = chatParticipantRepository
            .findChatThreadsByUserId(UUID.fromString(userId2));

        // Find the common thread between the two users
        return user1Threads.stream()
            .filter(user2Threads::contains)
            .findFirst();
    }

    public ChatThread createNewThread(String userId1, String userId2) {
        // Create a new chat thread
        ChatThread chatThread = new ChatThread();
        chatThread.setName("Chat between " + userId1 + " and " + userId2); // You can customize the name as needed
        chatThread = chatThreadRepository.save(chatThread);

        // Add both users as participants to the thread
        addParticipantToThread(userId1, chatThread);
        addParticipantToThread(userId2, chatThread);

        return chatThread;
    }

    private void addParticipantToThread(String userId, ChatThread chatThread) {
        ChatParticipant participant = new ChatParticipant();
        participant.setUser(userRepository.findById(UUID.fromString(userId)).orElse(null));
        participant.setChatThread(chatThread);
        chatParticipantRepository.save(participant);
    }


    public List<ChatThread> getAllThreads() {
        return chatThreadRepository.findAll();
    }
}
