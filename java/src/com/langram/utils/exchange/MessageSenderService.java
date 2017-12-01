package com.langram.utils.exchange;

import com.langram.utils.exchange.exception.UnsupportedSendingModeException;
import com.langram.utils.exchange.socket.UDPMulticastSocket;
import com.langram.utils.exchange.socket.UDPSocket;
import com.langram.utils.messages.Message;

import java.io.IOException;
import java.net.InetAddress;

public class MessageSenderService
{
    public enum SendingMode {
        UNICAST,
        MULTICAST
    }

    public void sendMessageOn(String ipAddress, int port, SendingMode sendingMode, Message message) throws UnsupportedSendingModeException, IOException {
        switch (sendingMode)
        {
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

    private void sendUnicastMessageOn(String ipAddress, int port, Message message) throws IOException {
        UDPSocket socket = new UDPSocket(5650);
        InetAddress rcvrAddress = InetAddress.getByName(ipAddress);
        socket.send(message, rcvrAddress, port);
        socket.close();
    }

    private void sendMulticastMessageOn(String ipAddress, int port, Message message) throws IOException {

        UDPMulticastSocket socket = null;

        try
        {
            socket = new UDPMulticastSocket(4711);
            socket.setTimeout(1000);
            System.out.println("Socket created...");

            socket.join(ipAddress);
            InetAddress srvrAddress = InetAddress.getByName(ipAddress);

            socket.send(message, srvrAddress, port);

            socket.leave(ipAddress);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        if (socket != null)
        {
            socket.close();
            System.out.println("Shutting down...");
        }

    }

}
