package com.langram.utils.messages;

import com.langram.utils.channels.Channel;

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
    private MessageType messageType;
    String senderName;
    Channel channel;

    Message(MessageType t, Channel c)
    {
        this.date = new Date();
        this.messageType = t;
        this.channel = c;
    }

    public String toString()
    {
        String type = this.messageType == MessageType.TEXT_MESSAGE ? "TEXT" : "FILE";
        return "["+this.date+"] : Un message de type " + type + " a été reçu !";
    }

    public String getDate() {
        return date.toString();
    }
    public String getSenderName() { return senderName; }
    public String getText() { return ""; }
    public MessageType getMessageType() { return messageType; }
    public Channel getChannel() { return channel; }
    public void updateChannel(Channel c) { this.channel = c; }
}
