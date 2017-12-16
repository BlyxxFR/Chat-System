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
import com.langram.utils.messages.FileMessage;
import com.langram.utils.messages.Message;
import com.langram.utils.messages.MessageDisplay;
import com.langram.utils.messages.TextMessage;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Pair;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.langram.utils.exchange.network.MessageSenderService.MULTICAST_PORT_LISTENER;
import static com.langram.utils.exchange.network.MessageSenderService.SendingMode.MULTICAST;
import static java.util.concurrent.TimeUnit.SECONDS;

public class MainController extends CommonController implements javafx.fxml.Initializable {

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
    public Label privateMessageLabel;
    public FontAwesomeIconView privateMessagesEnvelope;
    public AnchorPane sendingArea;
    public JFXListView<String> connectedUsersList;

    private ResourceBundle mainMessages;

    private static MainController instance;
    private ArrayList<String> listeningChannels = new ArrayList<>();

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

        // Set private message label
        int privateMessageCounter = User.getInstance().getPrivateMessageCount();
        privateMessageLabel.setText(String.format(mainMessages.getString(privateMessageCounter > 1 ? "new_message_plural" : "new_message"), privateMessageCounter));
        privateMessagesEnvelope.setGlyphName(privateMessageCounter > 0 ? "ENVELOPE_ALT" : "ENVELOPE_OPEN_ALT");

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
        for(Channel channel : channelsList) {
            projectsList.getItems().add(channel.getChannelName());
        }
        projectsList.getItems().sort(String::compareToIgnoreCase);
        Channel currentChannel = ChannelRepository.getInstance().getCurrentChannel();
        if(currentChannel != null)
            this.goToChannel(currentChannel.getChannelName());
    }

    public static MainController getInstance() {
        return instance;
    }

    public class onReceivedMessage implements IncomingMessageListener {
        public void onNewIncomingMessage(final Message message, String senderAddress, int senderPort) {
            Platform.runLater(
                    () -> {

                        if (message instanceof TextMessage || message instanceof FileMessage) {
                            Channel msgChannel = ChannelRepository.getInstance().getChannelWithIP(message.getChannel().getIpAddress());
                            message.updateChannel(msgChannel);
                            MessageRepository.getInstance().store(message);
                            if(ChannelRepository.getInstance().isActiveChannel(message.getChannel().getIpAddress()))
                            {
                                messagesList.getItems().add(message);
                                messagesList.scrollTo(message);
                            }
                        }
                    }
            );
        }
    }

    public class onReceivedPrivateMessage implements IncomingMessageListener {
        public void onNewIncomingMessage(final Message message, String senderAddress, int senderPort) {
            Platform.runLater(
                    () -> {

                    }
            );
        }
    }

    public void addAnUserToActiveChannelConnectedUsersList(final String username) {
        Platform.runLater(
                () -> {
                    if(!connectedUsersList.getItems().contains(username))
                    {
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

        scheduler.scheduleAtFixedRate(getConnectedUsersList, 2*60, 2*60, SECONDS);
    }

    public void addProjectChannel() {
        // Generate dialog box
        MainView.AddChannelDialog dialog = new MainView.AddChannelDialog();

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

    public void goToChannel() {
        this.goToChannel(projectsList.getSelectionModel().getSelectedItems().get(0));
    }

    private void goToChannel(String selectedChannel) {
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
            ArrayList<Message> messages = MessageRepository.getInstance().retrieveMessageFromChannel(ChannelRepository.getInstance().getChannelUUID(selectedChannel).toString());
            messagesList.getItems().addAll(messages);

            if(!listeningChannels.contains(selectedChannel)) {
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
}

