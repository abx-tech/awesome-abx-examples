package com.abx.chat.dto;

import com.abx.chat.model.ChatThread;
import java.util.UUID;

public class ChatThreadDTO {
    private String id;
    private String name;
    // You can add more fields as needed, such as a list of participants, last message, etc.

    // Constructors
    public ChatThreadDTO() {
    }

    public ChatThreadDTO(String id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Optionally, you can include methods to convert between the entity and the DTO
    public static ChatThreadDTO fromEntity(ChatThread chatThread) {
        return new ChatThreadDTO(chatThread.getId().toString(), chatThread.getName());
    }

    public ChatThread toEntity() {
        ChatThread chatThread = new ChatThread();
        chatThread.setId(UUID.fromString(this.id));
        chatThread.setName(this.name);
        return chatThread;
    }
}
