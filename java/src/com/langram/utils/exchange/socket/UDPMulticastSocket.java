package com.langram.utils.exchange.socket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class UDPMulticastSocket extends UDPSocket
{
    public UDPMulticastSocket(int port) throws IOException
    {
        super(new MulticastSocket(port));
    }

    public UDPMulticastSocket() throws IOException
    {
        super(new MulticastSocket());
    }

    public void join(String mcAddress) throws IOException
    {
        InetAddress ia = InetAddress.getByName(mcAddress);
        ((MulticastSocket) socket).joinGroup(ia);
    }

    public void leave(String mcAddress) throws IOException
    {
        InetAddress ia = InetAddress.getByName(mcAddress);
        ((MulticastSocket) socket).leaveGroup(ia);
    }
}