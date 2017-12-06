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
    DatagramSocket socket;
    private InetAddress senderAddress;
    private int senderPort;

    //constructors
    UDPSocket(DatagramSocket socket)
    {
        this.socket = socket;
    }

    public UDPSocket(int port) throws SocketException
    {
        this(new DatagramSocket(port));
    }

    public UDPSocket() throws SocketException {
        this(new DatagramSocket());
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

            inPacket.setLength(data.length);
            b_in.reset();

            messageListener.onNewIncomingMessage(message, senderAddress.toString().substring(1), senderPort);
        } catch (ClassNotFoundException e2) {
            e2.printStackTrace();
        }
    }

    public void close()
    {
        socket.close();
    }
}