package com.langram.utils.messages;

public class Message {

    private String senderName;
    private String date;
    private String text;

    public Message(String senderName, String date, String text) {
        this.text = text;
        this.date = date;
        this.senderName = senderName;
    }

    String getDate() {
        return date;
    }

    String getText() {
        return text;
    }

    String getSenderName() { return senderName; }

}
