package com.langram.utils.messages;

import com.langram.utils.channels.Channel;

import java.util.Date;

public class TextMessage extends Message
{
    private String UUID;
    private String content;

    public TextMessage(String UUID, String senderName, String c, Date d, Channel ch)
    {
        super(MessageType.TEXT_MESSAGE, ch);
        this.UUID = UUID;
        this.senderName = senderName;
        this.content = c;
        this.date = d;
    }

    public TextMessage(String username, String text, Channel currentChannel) {
        super(MessageType.TEXT_MESSAGE, currentChannel);
        this.senderName = username;
        this.content = text;
    }

    @Override
    public String getText()
    {
        return this.content;
    }

    public String getUUID() { return UUID; }
}
