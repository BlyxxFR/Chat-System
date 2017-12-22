package com.langram.utils.exchange.network.controller;

import com.langram.main.MainController;
import com.langram.utils.User;
import com.langram.utils.channels.ChannelRepository;
import com.langram.utils.exchange.network.IncomingMessageListener;
import com.langram.utils.exchange.network.MessageReceiverTask;
import com.langram.utils.exchange.network.MessageSenderService;
import com.langram.utils.messages.ControlMessage;
import com.langram.utils.messages.Message;
import javafx.util.Pair;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;

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
                    if (ChannelRepository.getInstance().isConnectedToChannel(controlMessage.getContent())) {
                        response = new ControlMessage(GetConnectedUsersToAChannelReply, User.getInstance().getUsername());
                        NetworkController.getInstance().reply(senderAddress, response);
                    }
                    break;
                case NotifyConnectionToChannel:
                    if (ChannelRepository.getInstance().isActiveChannel(controlMessage.getContent())) {
                        response = new ControlMessage(AcknowledgeNewConnection, User.getInstance().getUsername());
                        if (!User.getInstance().getUsername().equals(controlMessage.getSenderName()))
                            NetworkController.getInstance().reply(senderAddress, response);
                        MainController.getInstance().addAnUserToActiveChannelConnectedUsersList(controlMessage.getSenderName());
                    }
                    break;
                case AskIPForUnicastMessage:
                    String username = User.getInstance().getUsername();
                    if (controlMessage.getContent().equals(username)) {
                        Pair<String, Integer> data = MainController.getInstance().createListeningThread(username);
                        response = new ControlMessage(ReplyIPForUnicastMessage, data.getKey() + ":" + data.getValue());
                        NetworkController.getInstance().reply(senderAddress, response);
                    }
                default:
                    break;
            }
        }
    }
}
