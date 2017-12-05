package com.langram.utils.exchange.network;

interface MessageReceiver {
    void listenOnPort(String ipAddress, int port, IncomingMessageListener messageListener);
}
