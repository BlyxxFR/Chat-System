package com.langram.utils.messages;

import java.util.Date;

public class TextMessage extends Message
{
    private String content;

    public TextMessage(String senderName, String c)
    {
        super(MessageType.TEXT_MESSAGE);
        this.senderName = senderName;
        this.content = c;
    }

    public TextMessage(String senderName, String c, Date d)
    {
        super(MessageType.TEXT_MESSAGE);
        this.senderName = senderName;
        this.content = c;
        this.date = d;
    }

    @Override
    public String getText()
    {
        return this.content;
    }
}
