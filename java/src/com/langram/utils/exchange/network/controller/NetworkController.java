package com.langram.utils.exchange.network.controller;

import com.langram.utils.exchange.network.IncomingMessageListener;
import com.langram.utils.exchange.network.MessageReceiverThread;
import com.langram.utils.exchange.network.exception.UnsupportedMessageTypeException;
import com.langram.utils.messages.Message;
import javafx.application.Platform;

import static com.langram.utils.exchange.network.MessageSenderService.SendingMode.MULTICAST;

public class NetworkController {

    private static NetworkController instance = new NetworkController();
    private MessageReceiverThread backgroundThread;

    private enum NetworkControllerMessageType { CheckForUniqueUsername }

    public NetworkController() {
        this.backgroundThread = new MessageReceiverThread(MULTICAST, "224.0.0.1", 4488, new onReceivedControlMessage());
    }

    public static NetworkController getInstance() {
        return instance;
    }

    public void sendControlMessage(NetworkControllerMessageType type, String[] args) throws UnsupportedMessageTypeException {
        switch(type) {
            case CheckForUniqueUsername:

                break;
            default:
                throw new UnsupportedMessageTypeException();
        }
    }

    private void send

    private class onReceivedControlMessage implements IncomingMessageListener {
        public void onNewIncomingMessage(final Message message) {
            Platform.runLater(
                    () -> {

                    }
            );
        }
    }
}
