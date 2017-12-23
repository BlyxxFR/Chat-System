package com.langram.main;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import com.langram.utils.CommonController;
import com.langram.utils.Settings;
import com.langram.utils.User;
import com.langram.utils.channels.Channel;
import com.langram.utils.channels.ChannelRepository;
import com.langram.utils.channels.MessageRepository;
import com.langram.utils.exchange.network.IPAddressValidator;
import com.langram.utils.exchange.network.IncomingMessageListener;
import com.langram.utils.exchange.network.MessageReceiverTask;
import com.langram.utils.exchange.network.MessageSenderService;
import com.langram.utils.exchange.network.controller.NetworkController;
import com.langram.utils.exchange.network.controller.NetworkControllerAction;
import com.langram.utils.exchange.network.exception.UnsupportedSendingModeException;
import com.langram.utils.messages.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Pair;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.langram.utils.exchange.network.MessageSenderService.MULTICAST_PORT_LISTENER;
import static com.langram.utils.exchange.network.MessageSenderService.SendingMode.MULTICAST;
import static com.langram.utils.exchange.network.controller.NetworkControllerMessageType.CheckIfUserIsOnline;
import static com.langram.utils.exchange.network.controller.NetworkControllerMessageType.ReplyUserOnline;
import static java.util.concurrent.TimeUnit.SECONDS;

public class Controller extends CommonController implements javafx.fxml.Initializable {

    public Label projectsLabel;
    public JFXTextArea textMessage;
    public FontAwesomeIconView sendFile;
    public FontAwesomeIconView sendMessage;
    public ImageView profileImage;
    public JFXListView<Message> messagesList;
    public JFXListView<String> projectsList;
    public Label displayUsername;
    public Label onlineLabel;
    public Label channelName;
    public FontAwesomeIconView addProjectChannelIcon;
    public AnchorPane sendingArea;
    public JFXListView<String> connectedUsersList;

    private ResourceBundle mainMessages;

    private static Controller instance;
    private ArrayList<String> listeningChannels = new ArrayList<>();
    private HashMap<String, Integer> listeningPrivateChannels = new HashMap<>();

    private HashMap<String, Pair<String, Integer>> privateConversations = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        instance = this;
        NetworkController.getInstance().start();
        getConnectedUsersListOnActiveChannelPeriodically();

        // Load labels
        mainMessages = ResourceBundle.getBundle("MainMessagesBundle", Settings.getInstance().getLocale());
        projectsLabel.setText(mainMessages.getString("projects"));
        onlineLabel.setText(mainMessages.getString("online"));
        displayUsername.setText(User.getInstance().getUsername());

        // Set messages' list css
        String css = this.getClass().getResource("/com/langram/utils/resources/messages.css").toExternalForm();
        messagesList.getStylesheets().add(css);
        messagesList.setCellFactory(param -> new MessageDisplay());

        // Set projects' list css and click action
        css = this.getClass().getResource("/com/langram/utils/resources/projects.css").toExternalForm();
        projectsList.getStylesheets().add(css);

        // Set connected-user list css and click action
        css = this.getClass().getResource("/com/langram/utils/resources/projects.css").toExternalForm();
        connectedUsersList.getStylesheets().add(css);

        // Disable areas in case of no channel is added
        sendingArea.setDisable(true);

        // Load project list
        ArrayList<Channel> channelsList = ChannelRepository.getInstance().retrieveAll();
        for (Channel channel : channelsList) {
            projectsList.getItems().add(channel.getChannelName());
        }
        projectsList.getItems().sort(String::compareToIgnoreCase);
        Channel currentChannel = ChannelRepository.getInstance().getCurrentChannel();

        if (currentChannel != null)
            this.goToChannel(currentChannel.getChannelName());

        // Send not sent messages thread
        sendNotSentMessages();
    }

    public static Controller getInstance() {
        return instance;
    }

    // Change channel methods
    public void goToChannel() {
        if (projectsList.getSelectionModel().getSelectedItems().size() > 0)
            this.goToChannel(projectsList.getSelectionModel().getSelectedItems().get(0));
    }

    private void goToChannel(String selectedChannel) {

        this.sendMessage.setOnMouseClicked(event -> sendMessage());
        String ipAddress = ChannelRepository.getInstance().getChannelIP(selectedChannel);

        if (!ipAddress.equals("none")) {

            messagesList.getItems().clear();

            Platform.runLater(
                    () -> {
                        // Get connected users on channel
                        ArrayList<String> connectedUsers = NetworkControllerAction.getInstance().notifyChannelOfMyConnectionAndGetConnectedUsers(ipAddress);
                        connectedUsersList.getItems().clear();
                        connectedUsersList.getItems().addAll(connectedUsers);
                    }
            );

            ChannelRepository.getInstance().switchToChannel(selectedChannel);
            ArrayList<Message> messages = MessageRepository.getInstance().retrieveMessagesFromChannel(ChannelRepository.getInstance().getChannelUUID(selectedChannel));
            messagesList.getItems().addAll(messages);
            if(messagesList.getItems().size() > 0)
                messagesList.scrollTo(messagesList.getItems().size()-1);

            if (!listeningChannels.contains(selectedChannel)) {
                threadsPool.submit(new MessageReceiverTask(MULTICAST, ChannelRepository.getInstance().getCurrentChannel().getIpAddress(), MULTICAST_PORT_LISTENER, new onReceivedMessage()).get());
                listeningChannels.add(selectedChannel);
            }

            // Set prompt message
            sendingArea.setDisable(false);
            textMessage.setPromptText(String.format(mainMessages.getString("prompt"), selectedChannel));
            channelName.setText(selectedChannel);
        }
    }

    public void goToPrivateMessages() {
        if (connectedUsersList.getSelectionModel().getSelectedItems().size() > 0) {
            String user = connectedUsersList.getSelectionModel().getSelectedItems().get(0);
            this.sendMessage.setOnMouseClicked(event -> sendPrivateMessage(user));

            if (!this.privateConversations.containsKey(user)) {
                try {
                    String ipPort = NetworkControllerAction.getInstance().getIpAndPortToDiscussWithAnUser(user);

                    String ip = ipPort.split("/")[1].split(":")[0];
                    String port = ipPort.split("/")[1].split(":")[1];

                    ChannelRepository.getInstance().updateChannelIP(user, ip);

                    privateConversations.put(user, new Pair<>(ip, Integer.valueOf(port)));
                    Channel c = new Channel(user, ip);
                    ChannelRepository.getInstance().store(c, true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            sendingArea.setDisable(false);
            textMessage.setPromptText(String.format(mainMessages.getString("prompt"), user));
            channelName.setText(user);

            this.messagesList.getItems().clear();
            ArrayList<Message> messages = MessageRepository.getInstance().retrieveMessagesFromChannel(ChannelRepository.getInstance().getChannelUUID(user));
            messagesList.getItems().addAll(messages);

            (new Thread(
                    () -> {
                        Platform.runLater(
                                () -> {
                                    if(messagesList.getItems().size() > 0)
                                        messagesList.scrollTo(messagesList.getItems().size()-1);
                                }
                        );
                    }
            )).start();

        }
    }

    // Message sender
    private boolean checkIfMessageIsSent(String ip, int port, String user, Message message) {
        boolean sent = true;
        MessageSenderService messageSender = new MessageSenderService();
        try {
            if (NetworkControllerAction.getInstance().checkIfUserIsOnline(user, ip, port)) {
                messageSender.sendMessageOn(ip, port, MessageSenderService.SendingMode.UNICAST, message);
            } else {
                sent = false;
            }
        } catch (Exception | UnsupportedSendingModeException e) {
            sent = false;
        }
        return sent;
    }

    private void sendPrivateMessage(String user) {
        String ip = this.privateConversations.get(user).getKey();
        Integer port = this.privateConversations.get(user).getValue();
        String text = textMessage.getText();

        if (!text.isEmpty()) {
            Message message = new TextMessage(User.getInstance().getUsername(), text, ChannelRepository.getInstance().getChannelWithIP(ip));
            boolean sent = true;
            if (!user.equals(User.getInstance().getUsername())) {
                sent = checkIfMessageIsSent(ip, port, user, message);
            }
            messagesList.getItems().add(message);
            messagesList.scrollTo(message);
            message.updateChannel(ChannelRepository.getInstance().getChannelWithName(user));
            MessageRepository.getInstance().store(message, sent);
            this.textMessage.setText("");
        }
    }

    public void sendMessage() {
        if (!textMessage.getText().isEmpty()) {
            Message message = new TextMessage(User.getInstance().getUsername(), textMessage.getText(), ChannelRepository.getInstance().getCurrentChannel());
            MessageSenderService messageSender = new MessageSenderService();
            try {
                messageSender.sendMessageOn(ChannelRepository.getInstance().getCurrentChannel().getIpAddress(), MULTICAST_PORT_LISTENER, MessageSenderService.SendingMode.MULTICAST, message);
            } catch (UnsupportedSendingModeException | IOException e) {
                e.printStackTrace();
            }
            textMessage.setText("");
        }
    }

    // Message receivers
    public class onReceivedMessage implements IncomingMessageListener {
        public void onNewIncomingMessage(final Message message, String senderAddress, int senderPort) {
            Platform.runLater(
                    () -> {
                        if (message instanceof TextMessage || message instanceof FileMessage) {
                            displayTray(String.format(mainMessages.getString("notification_multicast"), message.getChannel().getChannelName()));
                            Channel msgChannel = ChannelRepository.getInstance().getChannelWithIP(message.getChannel().getIpAddress());
                            message.updateChannel(msgChannel);
                            MessageRepository.getInstance().store(message, true);
                            if (ChannelRepository.getInstance().isActiveChannel(message.getChannel().getIpAddress())) {
                                messagesList.getItems().add(message);
                                messagesList.scrollTo(message);
                            }
                        }
                    }
            );
        }
    }

    public class onReceivedPrivateMessage implements IncomingMessageListener {
        public void onNewIncomingMessage(final Message incommingMessage, String senderAddress, int senderPort) {
            Platform.runLater(
                    () -> {
                        if (incommingMessage.getMessageType() == Message.MessageType.CONTROL_MESSAGE) {
                            ControlMessage message = (ControlMessage) incommingMessage;
                            if (message.getControlType() == CheckIfUserIsOnline && message.getContent().equals(User.getInstance().getUsername())) {
                                ControlMessage response = new ControlMessage(ReplyUserOnline, "OK");
                                NetworkController.getInstance().reply(senderAddress, response);
                            }
                        }
                        if (incommingMessage.getMessageType() == Message.MessageType.TEXT_MESSAGE) {
                            TextMessage message = (TextMessage) incommingMessage;
                            String channelIP = ChannelRepository.getInstance().getChannelIP(message.getSenderName());
                            Channel channel;

                            if (channelIP != null && channelIP.equals(senderAddress)) {
                                channel = ChannelRepository.getInstance().getChannelWithIP(channelIP);
                            } else {
                                channel = new Channel(message.getSenderName(), senderAddress);
                                ChannelRepository.getInstance().store(channel, true);
                            }

                            message.updateChannel(channel);
                            MessageRepository.getInstance().store(message, true);

                            displayTray(String.format(mainMessages.getString("notification_unicast"), message.getChannel().getChannelName()));

                            if (ChannelRepository.getInstance().isActiveChannel(message.getChannel().getIpAddress())) {
                                messagesList.getItems().add(message);
                                messagesList.scrollTo(message);
                            }
                        }
                    }
            );
        }
    }

    // Misc
    public void addAnUserToActiveChannelConnectedUsersList(final String username) {
        Platform.runLater(
                () -> {
                    if (!connectedUsersList.getItems().contains(username)) {
                        connectedUsersList.getItems().add(username);
                        connectedUsersList.getItems().sort(String::compareToIgnoreCase);
                    }
                }
        );
    }

    private void getConnectedUsersListOnActiveChannelPeriodically() {
        final Runnable getConnectedUsersList = (() -> {

            if (ChannelRepository.getInstance().getCurrentChannel() != null) {
                Platform.runLater(() -> {
                            ArrayList<String> connectedUsers = NetworkControllerAction.getInstance().getConnectedUsersToAChannel(ChannelRepository.getInstance().getCurrentChannel().getIpAddress());
                            connectedUsersList.getItems().clear();
                            connectedUsersList.getItems().addAll(connectedUsers);
                        }
                );
            }

        });

        scheduler.scheduleAtFixedRate(getConnectedUsersList, 2 * 60, 2 * 60, SECONDS);
    }

    public void addProjectChannel() {
        // Generate dialog box
        View.AddChannelDialog dialog = new View.AddChannelDialog();

        // Request focus on the username field by default.
        Platform.runLater(() -> dialog.getNameField().requestFocus());

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.get().setResultConverter(dialogButton -> {
            if (dialogButton == dialog.getAddButton()) {
                return new Pair<>(dialog.getNameField().getText(), dialog.getIpField().getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.get().showAndWait();

        result.ifPresent(pair -> {
            IPAddressValidator ipAddressValidator = new IPAddressValidator();
            if (ipAddressValidator.validate(pair.getValue()) && !ChannelRepository.getInstance().isConnectedToChannel(pair.getValue())) {
                projectsList.getItems().add(pair.getKey());
                ChannelRepository.getInstance().store(new Channel(pair.getKey(), pair.getValue()));
            }
        });
        projectsList.getItems().sort(String::compareToIgnoreCase);
    }

    public Pair<String, Integer> createListeningThread(String username) {
        // Getting a free port
        DatagramSocket socket;
        try {
            socket = new DatagramSocket();
            String address = String.valueOf(InetAddress.getLocalHost());
            int port = socket.getLocalPort();
            socket.close();

            if (listeningPrivateChannels.containsKey(username)) {
                return new Pair<>(address, listeningPrivateChannels.get(username));
            }

            MessageReceiverTask messageReceiverTask = new MessageReceiverTask(MessageSenderService.SendingMode.UNICAST, "127.0.0.1", port, new onReceivedPrivateMessage());
            threadsPool.submit(messageReceiverTask.get());
            return new Pair<>(address, port);

        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }

        return new Pair<>(null, null);
    }

    private void sendNotSentMessages() {
        final Runnable sendNotSentMessagesThread = (() -> {

            if (ChannelRepository.getInstance().getCurrentChannel() != null) {
                Platform.runLater(() -> {
                            ArrayList<Message> notSentMessages = MessageRepository.getInstance().retrieveNotSentMessages();
                            MessageSenderService messageSender = new MessageSenderService();
                            for (Message message : notSentMessages) {
                                if (message instanceof TextMessage) {
                                    TextMessage textMessage = (TextMessage) message;
                                    try {
                                        Channel channel = message.getChannel();
                                        IPAddressValidator ipAddressValidator = new IPAddressValidator();
                                        if (ipAddressValidator.validate(channel.getIpAddress())) {
                                            messageSender.sendMessageOn(channel.getIpAddress(), MULTICAST_PORT_LISTENER, MessageSenderService.SendingMode.MULTICAST, message);
                                            MessageRepository.getInstance().updateSentStatus(textMessage.getUUID(), true);
                                        } else {
                                            if (privateConversations.containsKey(textMessage.getChannel().getChannelName())) {
                                                Pair<String, Integer> ipAndPort = privateConversations.get(textMessage.getChannel().getChannelName());
                                                boolean sent = checkIfMessageIsSent(ipAndPort.getKey(), ipAndPort.getValue(), textMessage.getChannel().getChannelName(), textMessage);
                                                MessageRepository.getInstance().updateSentStatus(textMessage.getUUID(), sent);
                                            }
                                        }
                                    } catch (UnsupportedSendingModeException | IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                );
            }

        });

        scheduler.scheduleAtFixedRate(sendNotSentMessagesThread, 0, 5 * 60, SECONDS);
    }

}

