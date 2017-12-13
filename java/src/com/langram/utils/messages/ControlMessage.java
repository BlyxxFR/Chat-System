package com.langram.utils.messages;

import com.langram.utils.User;
import com.langram.utils.exchange.network.controller.NetworkControllerMessageType;

public class ControlMessage extends Message {

    private NetworkControllerMessageType type;
    private String content = "";

    public ControlMessage(NetworkControllerMessageType type, String content)
    {
        super(MessageType.CONTROL_MESSAGE, null);
        this.senderName = (User.getInstance() != null) ? User.getInstance().getUsername() : "CONTROL";
        this.type = type;
        this.content = content;
    }

    public NetworkControllerMessageType getControlType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public String getSenderName() { return this.senderName; }
}
