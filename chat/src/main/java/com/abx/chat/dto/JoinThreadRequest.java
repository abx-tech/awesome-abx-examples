package com.abx.chat.dto;

public class JoinThreadRequest {
    private String userId1;
    private String userId2;
    // getters, setters, constructors

    public JoinThreadRequest() {
    }

    // getter and setter
    public String getUserId1() {
        return userId1;
    }

    public void setUserId1(String userId1) {
        this.userId1 = userId1;
    }

    public String getUserId2() {
        return userId2;
    }

    public void setUserId2(String userId2) {
        this.userId2 = userId2;
    }
}