package com.langram.utils.exchange.network;

import com.langram.utils.messages.Message;

import java.net.InetAddress;

public interface IncomingMessageListener {
    void onNewIncomingMessage(Message message, InetAddress senderAddress, int senderPort);
}