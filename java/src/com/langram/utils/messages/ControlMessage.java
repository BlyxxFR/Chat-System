package com.langram.utils.messages;

public class ControlMessage extends Message {

    private NetworkControllerMessageType type;
    private String content;

    public enum NetworkControllerMessageType { CheckForUniqueUsername, CheckForUniqueUsernameReply }

    public ControlMessage(NetworkControllerMessageType type, String content)
    {
        super(MessageType.CONTROL_MESSAGE);
        this.senderName = "CONTROL";
        this.type = type;
        this.content = content;
    }

    public NetworkControllerMessageType getControlType() {
        return type;
    }

    public String getContent() {
        return content;
    }
}
