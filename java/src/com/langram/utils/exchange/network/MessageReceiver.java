package com.langram.utils.exchange.network;

import java.io.IOException;

public interface MessageReceiver {
    void listenOnPort(String ipAddress, int port, IncomingMessageListener messageListener) throws IOException;
}
