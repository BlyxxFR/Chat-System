package com.langram.utils.exchange;

import com.langram.utils.exchange.socket.UDPMulticastSocket;

import java.io.IOException;

public class MulticastMessageReceiverService implements MessageReceiver {

    public static final int BUFFER_SIZE = 1024;

    public void listenOnPort(String ipAddress, int port, IncomingMessageListener messageListener) throws IOException {

        UDPMulticastSocket socket = null;

        try
        {
            socket = new UDPMulticastSocket(port);

            socket.join(ipAddress);

            while(true)
            {
                socket.receive(BUFFER_SIZE, messageListener);
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        if (socket != null)
            socket.close();
    }
}
