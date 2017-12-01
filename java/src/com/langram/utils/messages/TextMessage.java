package com.langram.utils.messages;

public class TextMessage extends Message
{
    private String content;

    public TextMessage(String senderName, String c)
    {
        super(MessageType.TEXT_MESSAGE);
        this.senderName = senderName;
        this.content = c;
    }

    public String getText()
    {
        return this.content;
    }
}
