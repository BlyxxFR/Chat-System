package com.langram.utils.exchange.network;

import com.langram.utils.exchange.network.socket.UDPSocket;

public class UnicastMessageReceiverService implements MessageReceiver {

    private static final int BUFFER_SIZE = 1024;

    public void listenOnPort(String ipAddress, int port, IncomingMessageListener messageListener) {
        UDPSocket socket = null;
        try {
            socket = new UDPSocket(port);
            while (true) socket.receive(BUFFER_SIZE, messageListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (socket != null)
            socket.close();
    }

}
