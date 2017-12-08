package com.langram.utils;

import java.util.ArrayList;

public class User {
    private static User instance = null;
    private String username = "";
    private ArrayList<String> activeChannelsList = new ArrayList<>();
    private String currentChannel = "none";

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
