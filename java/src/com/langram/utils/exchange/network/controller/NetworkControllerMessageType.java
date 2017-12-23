package com.langram.utils.exchange.network.controller;

public enum NetworkControllerMessageType {
    CheckForUniqueUsername,
    CheckForUniqueUsernameReply,
    GetConnectedUsersToAChannel,
    GetConnectedUsersToAChannelReply,
    NotifyConnectionToChannel,
    AcknowledgeNewConnection,
    AskIPForUnicastMessage,
    CheckIfUserIsOnline,
    ReplyUserOnline,
    ReplyIPForUnicastMessage
}

