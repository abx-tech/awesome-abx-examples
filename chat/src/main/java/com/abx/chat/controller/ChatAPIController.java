/*
 * Copyright  (c) 2023.  ABX
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.abx.chat.controller;

import com.abx.chat.dto.ChatThreadDTO;
import com.abx.chat.dto.UserDTO;
import com.abx.chat.model.ChatMessage;
import com.abx.chat.model.User;
import com.abx.chat.service.ChatThreadService;
import com.abx.chat.service.MessageService;
import com.abx.chat.service.UserService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The type Chat api controller.
 * <p>
 *     This class is the controller for the chat API.
 *     It is used to handle the requests for the chat API.
 * </p>
 */
@RestController
@RequestMapping("/api")
public class ChatAPIController {
    private final UserService userService;
    private final ChatThreadService chatThreadService;

    private final SimpMessagingTemplate template;
    private final MessageService messageService;

    /**
     * Instantiates a new Chat api controller.
     * @param userService It's used to handle the requests for the user API.
     * @param chatThreadService It's used to handle the requests for the chat thread API.
     * @param template It's used to send messages to the clients.
     * @param messageService It's used to handle the requests for the message API.
     */
    public ChatAPIController(UserService userService, ChatThreadService chatThreadService,
        SimpMessagingTemplate template, MessageService messageService) {
        this.userService = userService;
        this.chatThreadService = chatThreadService;
        this.template = template;
        this.messageService = messageService;
    }

    @PostMapping("/register")
    public User registerUser(@RequestBody UserDTO userDTO) {
        return userService.createUser(userDTO.toEntity());
    }

    @GetMapping("/user/{userId}")
    public List<ChatThreadDTO> getThreadsForUser(@PathVariable String userId) {
        return chatThreadService.getThreadsForUser(userId);
    }

    @GetMapping("/chat/contacts")
    public List<UserDTO> getContacts() {
        return userService.getAllUser().stream()
            .map(UserDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @GetMapping("/chat/chats")
    public List<ChatThreadDTO> getChats() {
        return chatThreadService.getAllThreads().stream()
            .map(ChatThreadDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @PostMapping("/api/threads/{threadId}/messages")
    public ResponseEntity<ChatMessage> receiveMessage(@PathVariable String threadId, @RequestBody ChatMessage message) {
        System.out.println("send message to thread" + threadId + " " + message);
        messageService.saveMessage(message); // Save to DynamoDB
        broadcastMessage("/topic/messages/" + threadId, message);
        return ResponseEntity.ok(message);
    }

    /**
     * Gets messages for thread.
     *
     * @param threadId the thread id
     * @return the messages for thread
     */
    @GetMapping("/api/threads/{threadId}/messages")
    public ResponseEntity<List<ChatMessage>> getMessagesForThread(@PathVariable String threadId) {
        System.out.println("get messages for thread" + threadId);
        return ResponseEntity.ok(messageService.getMessagesForThread(threadId));
    }


    /**
        * Broadcast message.
        *
        * @param threadId the thread id
        * @param message the message to broadcast
    */
    private void broadcastMessage(@DestinationVariable String threadId, ChatMessage message) {
        System.out.println("broadcast message to thread" + threadId + " " + message);
        template.convertAndSend("/topic/messages/" + threadId, message);
    }

}
