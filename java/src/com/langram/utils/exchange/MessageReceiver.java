package com.langram.utils.exchange;

import java.io.IOException;

public interface MessageReceiver {
    public void listenOnPort(String ipAdress, int port, IncomingMessageListener messageListener) throws IOException;
}
