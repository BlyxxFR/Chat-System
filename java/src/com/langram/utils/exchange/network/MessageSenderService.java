package com.langram.utils.exchange.network;

import com.langram.utils.exchange.network.exception.UnsupportedSendingModeException;
import com.langram.utils.exchange.network.socket.UDPMulticastSocket;
import com.langram.utils.exchange.network.socket.UDPSocket;
import com.langram.utils.messages.ControlMessage;
import com.langram.utils.messages.Message;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

public class MessageSenderService {

    private static final int BUFFER_SIZE = 1024;

    public enum SendingMode {
        UNICAST,
        MULTICAST
    }

    public final static int CONTROL_PORT_LISTENER = 7655;
    public final static int UNICAST_PORT_LISTENER = 5606;
    public final static int MULTICAST_PORT_LISTENER = 4488;
    private final static int TIMEOUT = 1000;

    public void sendMessageOn(String ipAddress, int port, SendingMode sendingMode, Message message) throws UnsupportedSendingModeException, IOException {
        switch (sendingMode) {
            case UNICAST:
                sendUnicastMessageOn(ipAddress, port, message);
                break;
            case MULTICAST:
                sendMulticastMessageOn(ipAddress, port, message);
                break;
            default:
                throw new UnsupportedSendingModeException();
        }
    }

    public ArrayList<ControlMessage> sendMessageOnAndListenForReply(String ipAddress, int port, SendingMode sendingMode, Message message) throws UnsupportedSendingModeException, IOException {
        sendMessageOn(ipAddress, port, sendingMode, message);
        return getReplies();
    }

    private void sendUnicastMessageOn(String ipAddress, int port, Message message) throws IOException {
        UDPSocket socket = new UDPSocket();
        InetAddress rcvrAddress = InetAddress.getByName(ipAddress);
        socket.send(message, rcvrAddress, port);
        socket.close();
    }

    private void sendMulticastMessageOn(String ipAddress, int port, Message message) throws IOException {
        UDPMulticastSocket socket = new UDPMulticastSocket();
        socket.setTimeout(TIMEOUT);
        socket.join(ipAddress);
        InetAddress srvrAddress = InetAddress.getByName(ipAddress);
        socket.send(message, srvrAddress, port);
        socket.leave(ipAddress);
        socket.close();
    }

    private ArrayList<ControlMessage> getReplies() throws IOException {
        ArrayList<ControlMessage> repliesList = new ArrayList<>();
        UDPSocket socket = new UDPSocket(CONTROL_PORT_LISTENER);
        socket.setTimeout(100);

        int MAX_CONSECUTIVE_TIMEOUT_ALLOWED = 3;
        int currentConsecutiveTimeout = 0;

        while(currentConsecutiveTimeout <= MAX_CONSECUTIVE_TIMEOUT_ALLOWED) {
            try {
                socket.receive(BUFFER_SIZE, new onReceivedReply(repliesList));
                currentConsecutiveTimeout = 0;
            } catch(IOException e) { currentConsecutiveTimeout++; }
        }

        socket.close();
        return repliesList;
    }

    private class onReceivedReply implements IncomingMessageListener {
        private ArrayList<ControlMessage> repliesList;

        onReceivedReply(ArrayList<ControlMessage> repliesList) {
            this.repliesList = repliesList;
        }
        public void onNewIncomingMessage(Message message, String senderAddress, int senderPort) {
            ControlMessage controlMessage = (ControlMessage) message;
            this.repliesList.add(controlMessage);
        }
    }

}
