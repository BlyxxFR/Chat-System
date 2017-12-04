package com.langram.utils.exchange.network;

import javafx.concurrent.Task;

import java.io.IOException;

import static com.langram.utils.exchange.network.MessageSenderService.SendingMode.MULTICAST;

public class MessageReceiverThread {

    private Thread backgroundThread;

    public MessageReceiverThread(MessageSenderService.SendingMode sendingMode, String ipAddress, int port, IncomingMessageListener incomingMessageListener) {
        Task<Void> backgroundTask = new Task<Void>() {
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
                try {
                    messageHandler.listenOnPort(ipAddress, port, incomingMessageListener);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        this.backgroundThread = new Thread(backgroundTask);
    }

    public void start() { this.backgroundThread.start(); }

    public void stop() { this.backgroundThread.interrupt(); }

    public Thread.State status() { return this.backgroundThread.getState(); }

}