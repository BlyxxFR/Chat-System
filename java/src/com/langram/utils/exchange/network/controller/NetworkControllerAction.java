package com.langram.utils.exchange.network.controller;

import com.langram.utils.exchange.network.MessageSenderService;
import com.langram.utils.messages.ControlMessage;
import com.langram.utils.messages.Message;

import java.util.ArrayList;

import static com.langram.utils.exchange.network.controller.NetworkControllerMessageType.*;

public class NetworkControllerAction {
    private final static String BROADCAST_IP = "224.0.0.1";
    private static NetworkControllerAction instance = new NetworkControllerAction();

    public static NetworkControllerAction getInstance() {
        return instance;
    }

    public boolean isAnUsernameAvailable(String username) {
        Message message = new ControlMessage(CheckForUniqueUsername, username);
        ArrayList<ControlMessage>replies = NetworkController.getInstance().sendAndGetReplies(BROADCAST_IP, message);
        for (ControlMessage m : replies) {
            if (m.getContent().equals("KO"))
                return false;
        }
        return true;
    }

    public ArrayList<String> getConnectedUsersToAChannel(String ipAddress) {
        Message message = new ControlMessage(GetConnectedUsersToAChannel, ipAddress);
        return parseUsersFromReplies(NetworkController.getInstance().sendAndGetReplies(BROADCAST_IP, message));
    }

    public ArrayList<String> notifyChannelOfMyConnectionAndGetConnectedUsers(String ipAddress) {
        Message message = new ControlMessage(NotifyConnectionToChannel, ipAddress);
        return parseUsersFromReplies(NetworkController.getInstance().sendAndGetReplies(BROADCAST_IP, message));
    }

    public String getIpAndPortToDiscussWithAnUser(String username) throws Exception {
        Message message = new ControlMessage(AskIPForUnicastMessage, username);
        ArrayList<ControlMessage> replies = NetworkController.getInstance().sendAndGetReplies(BROADCAST_IP, message);
        if(replies.size() > 0) {
            return replies.get(0).getContent();
        }
        throw new Exception();
    }

    private ArrayList<String> parseUsersFromReplies(ArrayList<ControlMessage> replies) {
        ArrayList<String> connectedUsers = new ArrayList<>();
        for (ControlMessage m : replies) {
            connectedUsers.add(m.getContent());
        }
        connectedUsers.sort(String::compareToIgnoreCase);
        return connectedUsers;
    }

    public boolean checkIfUserIsOnline(String user, String ip, int port) {
        Message message = new ControlMessage(CheckIfUserIsOnline, user);
        ArrayList<ControlMessage> replies = NetworkController.getInstance().sendAndGetReplies(ip, port, MessageSenderService.SendingMode.UNICAST, message);
        return replies.size() > 0;
    }
}
