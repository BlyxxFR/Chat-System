package com.langram.utils.exchange.network;

import com.langram.utils.messages.Message;

public interface IncomingMessageListener {
    void onNewIncomingMessage(Message message, String senderAddress, int senderPort);
}