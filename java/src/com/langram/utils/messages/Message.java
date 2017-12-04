package com.langram.utils.messages;

import java.io.Serializable;
import java.util.Date;

public abstract class Message implements Serializable
{
    public enum MessageType {
        TEXT_MESSAGE,
        FILE_MESSAGE,
        CONTROL_MESSAGE
    }

    protected Date date;
    protected MessageType messageType;
    protected String senderName;

    public Message(MessageType t)
    {
        this.date = new Date();
        this.messageType = t;
    }

    public String toString()
    {
        String type = this.messageType == MessageType.TEXT_MESSAGE ? "TEXT" : "FILE";
        return "["+this.date+"] : Un message de type " + type + " a été reçu !";
    }

    String getDate() {
        return date.toString();
    }
    String getSenderName() { return senderName; }

    String getText() { return ""; }
}
