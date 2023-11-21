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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
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
    private static final Logger logger = LoggerFactory.getLogger(ChatAPIController.class);

    private final UserService userService;
    private final ChatThreadService chatThreadService;
    private final SimpMessagingTemplate template;
    private final MessageService messageService;

    /**
     * Constructor for ChatAPIController.
     *
     * @param userService        Service for user-related operations.
     * @param chatThreadService  Service for chat thread operations.
     * @param template           Messaging template for sending messages.
     * @param messageService     Service for message-related operations.
     */
    public ChatAPIController(UserService userService, ChatThreadService chatThreadService,
        SimpMessagingTemplate template, MessageService messageService) {
        this.userService = userService;
        this.chatThreadService = chatThreadService;
        this.template = template;
        this.messageService = messageService;
    }

    @PostMapping("/users/register")
    public User registerUser(@RequestBody UserDTO userDTO) {
        return userService.createUser(userDTO.toEntity());
    }

    @GetMapping("/users/{userId}/threads")
    public List<ChatThreadDTO> getThreadsForUser(@PathVariable String userId) {
        return chatThreadService.getThreadsForUser(userId);
    }

    @GetMapping("/users/contacts")
    public List<UserDTO> getContacts() {
        return userService.getAllUser().stream().map(UserDTO::fromEntity).collect(Collectors.toList());
    }

    @GetMapping("/threads")
    public List<ChatThreadDTO> getChats() {
        return chatThreadService.getAllThreads().stream().map(ChatThreadDTO::fromEntity).collect(Collectors.toList());
    }

    @MessageMapping("/threads/{threadId}/messages")
    public ResponseEntity<ChatMessage> receiveMessage(@DestinationVariable String threadId, @RequestBody ChatMessage message) {
        log("Received message in thread: " + threadId);
        messageService.saveMessage(message); // Save to database
        broadcastMessage("/topic/messages/" + threadId, message);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/threads/{threadId}/messages")
    public ResponseEntity<List<ChatMessage>> getMessagesForThread(@PathVariable String threadId) {
        log("Fetching messages for thread: " + threadId);
        return ResponseEntity.ok(messageService.getMessagesForThread(threadId));
    }

    private void broadcastMessage(String destination, ChatMessage message) {
        log("Broadcasting message to: " + destination);
        template.convertAndSend(destination, message);
    }

    private void log(String message) {
        System.out.println(message);
        logger.info(message);
    }
}
