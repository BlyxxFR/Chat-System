package com.langram.utils.exchange.network.controller;

import com.langram.main.MainController;
import com.langram.utils.User;
import com.langram.utils.exchange.network.IncomingMessageListener;
import com.langram.utils.messages.ControlMessage;
import com.langram.utils.messages.Message;

import static com.langram.utils.exchange.network.controller.NetworkControllerMessageType.*;

class NetworkControllerMessageReceiver {

    static class onReceivedControlMessage implements IncomingMessageListener {
        public void onNewIncomingMessage(Message message, String senderAddress, int senderPort) {
            ControlMessage controlMessage = (ControlMessage) message;
            ControlMessage response;
            switch (controlMessage.getControlType()) {
                case CheckForUniqueUsername:
                    // TODO : Check if it's a known user instead of just current one
                    response = new ControlMessage(CheckForUniqueUsernameReply, (User.getInstance().getUsername().equals(controlMessage.getContent()) ? "KO" : "OK"));
                    NetworkController.getInstance().reply(senderAddress, response);
                    break;
                case GetConnectedUsersToAChannel:
                    if(User.getInstance().isConnectedToChannel(controlMessage.getContent())) {
                        response = new ControlMessage(GetConnectedUsersToAChannelReply, User.getInstance().getUsername());
                        NetworkController.getInstance().reply(senderAddress, response);
                    }
                    break;
                case NotifyConnectionToChannel:
                    if(User.getInstance().getActiveChannel().equals(controlMessage.getContent())) {
                        response = new ControlMessage(AcknowledgeNewConnection, User.getInstance().getUsername());
                        if(!User.getInstance().getUsername().equals(controlMessage.getSenderName()))
                            NetworkController.getInstance().reply(senderAddress, response);
                        MainController.getInstance().addAnUserToActiveChannelConnectedUsersList(controlMessage.getSenderName());
                    }
                default:
                    break;
            }
        }
    }
}
