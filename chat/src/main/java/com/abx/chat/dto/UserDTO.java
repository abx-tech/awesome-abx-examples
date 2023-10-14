package com.abx.chat.dto;


import com.abx.chat.model.User;
import java.util.UUID;

public class UserDTO {
    private String id;
    private String username;
    private String password;

    // Constructors
    public UserDTO() {
    }

    public UserDTO(String id, String username) {
        this.id = id;
        this.username = username;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Optionally, you can include methods to convert between the entity and the DTO
    public static UserDTO fromEntity(User user) {
        return new UserDTO(user.getId().toString(), user.getUsername());
    }

    public User toEntity() {
        User user = new User();
        if(this.id != null) {
            user.setId(UUID.fromString(this.id));
        }
        if(this.password != null) {
            user.setPassword(this.password);
        }

        user.setUsername(this.username);
        return user;
    }
}


