package com.langram.utils.messages;

public class ControlMessage extends Message {

    private NetworkControllerMessageType type;
    private String content;

    public enum NetworkControllerMessageType { CheckForUniqueUsername }

    public ControlMessage(NetworkControllerMessageType type, String content)
    {
        super(MessageType.CONTROL_MESSAGE);
        this.senderName = "CONTROL";
        this.type = type;
    }

}
