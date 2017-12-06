package com.langram.utils.exchange.network;

import javafx.concurrent.Task;

import static com.langram.utils.exchange.network.MessageSenderService.SendingMode.MULTICAST;

public class MessageReceiverTask {

    private Task<Void> backgroundTask;

    public MessageReceiverTask(MessageSenderService.SendingMode sendingMode, String ipAddress, int port, IncomingMessageListener incomingMessageListener) {
        this.backgroundTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                return null;
            }

            @Override
            public void run() {
                MessageReceiver messageHandler;
                if (sendingMode == MULTICAST)
                    messageHandler = new MulticastMessageReceiverService();
                else
                    messageHandler = new UnicastMessageReceiverService();
                messageHandler.listenOnPort(ipAddress, port, incomingMessageListener);

            }
        };
    }

    public Task<Void> get() { return this.backgroundTask; }

}