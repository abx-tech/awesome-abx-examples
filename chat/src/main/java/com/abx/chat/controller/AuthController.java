package com.abx.chat.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/sign-in")
    public Map<String, Object> signin() {
        Map<String, Object> response = new HashMap<>();
        response.put("uuid", "some-uuid");
        response.put("from", "some-from");
        response.put("password", "some-password");
        response.put("role", "some-role");

        Map<String, Object> data = new HashMap<>();
        data.put("displayName", "some-displayName");
        data.put("photoURL", "some-photoURL");
        data.put("email", "some-email");

        Map<String, Object> settings = new HashMap<>();
        settings.put("layout", new HashMap<>());
        settings.put("theme", new HashMap<>());

        data.put("settings", settings);
        data.put("shortcuts", Arrays.asList("shortcut1", "shortcut2"));

        response.put("data", data);
        System.out.println("response: " + response);

        return response;
    }
}
