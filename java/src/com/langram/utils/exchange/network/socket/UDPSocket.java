package com.langram.utils.exchange.network.socket;

import com.langram.utils.exchange.network.IncomingMessageListener;
import com.langram.utils.messages.Message;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPSocket
{
    private static final int BUFFER_SIZE = 1024;
    protected DatagramSocket socket;
    private InetAddress senderAddress;
    private int senderPort;

    //constructors
    protected UDPSocket(DatagramSocket socket)
    {
        this.socket = socket;
    }

    public UDPSocket() throws SocketException
    {
        this(new DatagramSocket());
    }

    public UDPSocket(int port) throws SocketException
    {
        this(new DatagramSocket(port));
    }

    //getters
    public InetAddress getSenderAddress()
    {
        return senderAddress;
    }

    public int getSenderPort()
    {
        return senderPort;
    }

    //setters
    public void setTimeout(int timeout) throws SocketException
    {
        socket.setSoTimeout(timeout);
    }

    //methods
    public void send(Message message, InetAddress rcvrAddress, int rcvrPort) throws IOException
    {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ObjectOutputStream stream = new ObjectOutputStream(byteStream);
        stream.writeObject(message);
        byte[] data = byteStream.toByteArray();
        DatagramPacket packet = new DatagramPacket(data, data.length, rcvrAddress, rcvrPort);
        socket.send(packet);
    }

    public void receive(int maxBytes, IncomingMessageListener messageListener) throws IOException
    {
        byte[] data = new byte[maxBytes];
        DatagramPacket inPacket = new DatagramPacket(data, 0, data.length);
        socket.receive(inPacket);

        senderAddress = inPacket.getAddress();
        senderPort = inPacket.getPort();

        ByteArrayInputStream b_in = new ByteArrayInputStream(data);
        ObjectInputStream o_in = new ObjectInputStream(b_in);

        try {
            Message message = (Message) o_in.readObject();

            inPacket.setLength(data.length); // must reset length field!
            b_in.reset(); // reset so next read is from start of byte[] again

            messageListener.onNewIncomingMessage(message);
        } catch (ClassNotFoundException e2) {
            e2.printStackTrace();
        }
    }

    public void close()
    {
        socket.close();
    }
}