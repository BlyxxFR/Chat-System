package com.langram.utils.exchange;

import com.langram.utils.exchange.socket.UDPSocket;

import java.io.IOException;

public class UnicastMessageReceiverService implements MessageReceiver {

    private static final int BUFFER_SIZE = 1024;

    public void listenOnPort(String ipAddress, int port, IncomingMessageListener messageListener) throws IOException {
        UDPSocket socket = null;
        try {
            socket = new UDPSocket(port);
            while (true) {
                socket.receive(BUFFER_SIZE, messageListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (socket != null)
            socket.close();
    }
}
