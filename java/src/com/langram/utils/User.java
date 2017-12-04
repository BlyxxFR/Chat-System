package com.langram.utils;

public class User {
    private static User instance = null;
    private String username;

    public User(String username) {
        instance = this;
        this.username = username;
    }

    public static User getInstance() { return instance; }

    public String getUsername() { return this.username; }

    public int getPrivateMessageCount() {
        return 3;
    }
}
