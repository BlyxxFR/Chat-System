package com.langram.utils.exchange.network.controller;

import com.langram.utils.exchange.network.MessageReceiverThread;
import com.langram.utils.exchange.network.MessageSenderService;
import com.langram.utils.exchange.network.exception.UnsupportedMessageTypeException;
import com.langram.utils.exchange.network.exception.UnsupportedSendingModeException;
import com.langram.utils.messages.ControlMessage;
import com.langram.utils.messages.ControlMessage.NetworkControllerMessageType;
import com.langram.utils.messages.Message;

import java.io.IOException;
import java.util.ArrayList;

import static com.langram.utils.exchange.network.MessageSenderService.CONTROL_PORT_LISTENER;
import static com.langram.utils.exchange.network.MessageSenderService.MULTICAST_PORT_LISTENER;
import static com.langram.utils.exchange.network.MessageSenderService.SendingMode.MULTICAST;
import static com.langram.utils.messages.ControlMessage.NetworkControllerMessageType.CheckForUniqueUsername;

public class NetworkController {

    private static NetworkController instance = new NetworkController();
    private MessageReceiverThread backgroundThread;

    public void start() {
        if (backgroundThread == null || backgroundThread.status() == Thread.State.NEW) {
            this.backgroundThread = new MessageReceiverThread(MULTICAST, "224.0.0.1", MULTICAST_PORT_LISTENER, new NetworkControllerMessageReceiver.onReceivedControlMessage());
            this.backgroundThread.start();
        }
    }

    public static NetworkController getInstance() {
        return instance;
    }

    public boolean sendControlMessage(NetworkControllerMessageType type, String[] args) throws UnsupportedMessageTypeException {

        switch (type) {
            case CheckForUniqueUsername:
                Message message = new ControlMessage(CheckForUniqueUsername, args[0]);
                ArrayList<ControlMessage> replies = this.sendAndWait(message);
                for(ControlMessage m : replies) {
                    if(m.getContent().equals("KO"))
                        return false;
                }
                return true;
            default:
                throw new UnsupportedMessageTypeException();
        }
    }

    private ArrayList<ControlMessage> sendAndWait(Message message) {
        MessageSenderService messageSender = new MessageSenderService();
        ArrayList<ControlMessage> replies = new ArrayList<>();

        try {
            replies = messageSender.sendMessageOnAndListenForReply("224.0.0.1", MULTICAST_PORT_LISTENER, MessageSenderService.SendingMode.MULTICAST, message);
        } catch (UnsupportedSendingModeException | IOException e) {
            e.printStackTrace();
        }

        return replies;
    }

    void reply(String senderAddress, Message message) {
        MessageSenderService messageSender = new MessageSenderService();
        try {
            messageSender.sendMessageOn(senderAddress, CONTROL_PORT_LISTENER, MessageSenderService.SendingMode.UNICAST, message);
        } catch (UnsupportedSendingModeException | IOException e) {
            e.printStackTrace();
        }
    }



}
