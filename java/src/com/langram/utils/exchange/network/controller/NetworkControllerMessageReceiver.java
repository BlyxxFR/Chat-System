package com.langram.utils.exchange.network.controller;

import com.langram.utils.User;
import com.langram.utils.exchange.network.IncomingMessageListener;
import com.langram.utils.messages.ControlMessage;
import com.langram.utils.messages.Message;

import java.net.InetAddress;

import static com.langram.utils.messages.ControlMessage.NetworkControllerMessageType.CheckForUniqueUsernameReply;

public class NetworkControllerMessageReceiver {

    static class onReceivedControlMessage implements IncomingMessageListener {
        public void onNewIncomingMessage(Message message, InetAddress senderAddress, int senderPort) {
            ControlMessage controlMessage = (ControlMessage) message;
            switch (controlMessage.getControlType()) {
                case CheckForUniqueUsername:
                    System.out.println(User.getInstance().getUsername() + " " + controlMessage.getContent());
                    String status = (User.getInstance().getUsername().equals(controlMessage.getContent()) ? "KO" : "OK");
                    System.out.println(status);
                    ControlMessage response = new ControlMessage(CheckForUniqueUsernameReply, status);
                    NetworkController.getInstance().reply(senderAddress.toString().substring(1), response);
                    break;
                default:
                    break;
            }
        }
    }

}
