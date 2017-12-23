package com.langram.utils.exchange.network.controller;

import com.langram.utils.exchange.network.MessageReceiverTask;
import com.langram.utils.exchange.network.MessageSenderService;
import com.langram.utils.exchange.network.exception.UnsupportedSendingModeException;
import com.langram.utils.messages.ControlMessage;
import com.langram.utils.messages.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.langram.utils.exchange.network.MessageSenderService.CONTROL_PORT_LISTENER;
import static com.langram.utils.exchange.network.MessageSenderService.MULTICAST_PORT_LISTENER;
import static com.langram.utils.exchange.network.MessageSenderService.SendingMode.MULTICAST;

public class NetworkController {

    private final static String BROADCAST_IP = "224.0.0.1";
    private static NetworkController instance = new NetworkController();
    private ExecutorService threadPool;

    public void start() {
        if (threadPool == null) {
            this.threadPool = Executors.newSingleThreadExecutor();
            this.threadPool.submit(new MessageReceiverTask(MULTICAST, BROADCAST_IP, MULTICAST_PORT_LISTENER, new NetworkControllerMessageReceiver.onReceivedControlMessage()).get());
        }
    }

    public static NetworkController getInstance() {
        return instance;
    }

    ArrayList<ControlMessage> sendAndGetReplies(String ipAddress, Message message) {
        return sendAndGetReplies(ipAddress, MULTICAST_PORT_LISTENER, MessageSenderService.SendingMode.MULTICAST, message);
    }

    ArrayList<ControlMessage> sendAndGetReplies(String ip, int port, MessageSenderService.SendingMode sendingMode, Message message) {
        MessageSenderService messageSender = new MessageSenderService();
        ArrayList<ControlMessage> replies = new ArrayList<>();
        try {
            replies = messageSender.sendMessageOnAndListenForReply(ip, port, sendingMode, message);
        } catch (UnsupportedSendingModeException | IOException e) {
            e.printStackTrace();
        }
        return replies;
    }

    public void reply(String senderAddress, Message message) {
        MessageSenderService messageSender = new MessageSenderService();
        try {
            messageSender.sendMessageOn(senderAddress, CONTROL_PORT_LISTENER, MessageSenderService.SendingMode.UNICAST, message);
        } catch (UnsupportedSendingModeException | IOException e) {
            e.printStackTrace();
        }
    }

}
