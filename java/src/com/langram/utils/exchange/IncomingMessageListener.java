package com.langram.utils.exchange;

import com.langram.utils.messages.Message;

public interface IncomingMessageListener {
    void onNewIncomingMessage(Message message);
}