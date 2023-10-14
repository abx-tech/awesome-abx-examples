package com.abx.chat.controller;

import com.abx.chat.dto.ChatThreadDTO;
import com.abx.chat.dto.JoinThreadRequest;
import com.abx.chat.dto.UserDTO;
import com.abx.chat.model.User;
import com.abx.chat.service.ChatThreadService;
import com.abx.chat.service.UserService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ChatAPIController {
    private final UserService userService;

    private final ChatThreadService chatThreadService;

    public ChatAPIController(UserService userService, ChatThreadService chatThreadService) {
        this.userService = userService;
        this.chatThreadService = chatThreadService;
    }

    @PostMapping("/register")
    public User registerUser(@RequestBody UserDTO userDTO) {
        return userService.createUser(userDTO.toEntity());
    }

    @GetMapping("/user/{userId}")
    public List<ChatThreadDTO> getThreadsForUser(@PathVariable String userId) {
        return chatThreadService.getThreadsForUser(userId);
    }

    @PostMapping("/join")
    public ChatThreadDTO joinThread(@RequestBody JoinThreadRequest request) {
        return chatThreadService.joinThread(request.getUserId1(), request.getUserId2());
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

}
