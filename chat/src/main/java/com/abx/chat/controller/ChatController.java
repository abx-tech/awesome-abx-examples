package com.abx.chat.controller;

import com.abx.chat.model.ChatMessage;
import com.abx.chat.service.MessageService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class ChatController {

    private final SimpMessagingTemplate template;
    private final MessageService messageService;

    public ChatController(SimpMessagingTemplate template, MessageService messageService) {
        this.template = template;
        this.messageService = messageService;
    }

    // WebSocket endpoint to broadcast messages to connected clients
    @MessageMapping("/sendMessage/{threadId}")
    public void broadcastMessage(@DestinationVariable String threadId, ChatMessage message) {
        System.out.println("broadcast message to thread" + threadId + " " + message);
        template.convertAndSend("/topic/messages/" + threadId, message);
    }


    // REST endpoint to receive and save messages
    @PostMapping("/api/threads/{threadId}/messages")
    public ResponseEntity<ChatMessage> receiveMessage(@PathVariable String threadId, @RequestBody ChatMessage message) {
        System.out.println("send message to thread" + threadId + " " + message);
        messageService.saveMessage(message); // Save to DynamoDB
        broadcastMessage("/topic/messages/" + threadId, message);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/api/threads/{threadId}/messages")
    public ResponseEntity<List<ChatMessage>> getMessagesForThread(@PathVariable String threadId) {
        System.out.println("get messages for thread" + threadId);
        return ResponseEntity.ok(messageService.getMessagesForThread(threadId));
    }

}



