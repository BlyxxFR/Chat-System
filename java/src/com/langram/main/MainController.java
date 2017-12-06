package com.langram.main;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import com.langram.utils.CommonController;
import com.langram.utils.Settings;
import com.langram.utils.User;
import com.langram.utils.exchange.network.IPAddressValidator;
import com.langram.utils.exchange.network.IncomingMessageListener;
import com.langram.utils.exchange.network.MessageReceiverThread;
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
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

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

    private String currentChannel = null;
    private ResourceBundle mainMessages;
    private HashMap<String, MessageReceiverThread> listenerMap = new HashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private static MainController instance;

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
        //projectsList.getItems().add(messagesList.getItems().size(), currentChannel);

    }

    public static MainController getInstance() {
        return instance;
    }

    public class onReceivedMessage implements IncomingMessageListener {
        public void onNewIncomingMessage(final Message message, String senderAddress, int senderPort) {
            Platform.runLater(
                    () -> {
                        if (message instanceof TextMessage || message instanceof FileMessage) {
                            messagesList.getItems().add(message);
                            messagesList.scrollTo(message);
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
                    connectedUsersList.getItems().add(username);
                    connectedUsersList.getItems().sort(String::compareToIgnoreCase);
                }
        );
    }

    private void getConnectedUsersListOnActiveChannelPeriodically() {
        final Runnable getConnectedUsersList = (() -> {
            if (!User.getInstance().getActiveChannel().equals("none")) {
                Platform.runLater(() -> {
                            ArrayList<String> connectedUsers = NetworkControllerAction.getInstance().getConnectedUsersToAChannel(User.getInstance().getActiveChannel());
                            connectedUsersList.getItems().clear();
                            connectedUsersList.getItems().addAll(connectedUsers);
                        }
                );
            }
        });

        scheduler.scheduleAtFixedRate(getConnectedUsersList, 2*60, 2*60, SECONDS);
    }

    public void addProjectChannel() {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.setTitle(Settings.getInstance().getAppName());
        dialog.setHeaderText(CommonController.getGlobalMessagesBundle().getString("addProjectPrompt"));

        Optional<String> result = dialog.showAndWait();
        String entered = "none";
        if (result.isPresent()) {
            entered = result.get();
        }
        IPAddressValidator ipAddressValidator = new IPAddressValidator();
        if (ipAddressValidator.validate(entered)) {
            projectsList.getItems().add(entered);
        }
    }

    public void goToChannel() {
        String ipAddress = projectsList.getSelectionModel().getSelectedItems().get(0);
        if (ipAddress != null) {
            // TODO : Change to thread pool
            if (!listenerMap.containsKey(ipAddress)) {
                // Start thread listener
                MessageReceiverThread listenerThread = new MessageReceiverThread(MULTICAST, ipAddress, MULTICAST_PORT_LISTENER, new onReceivedMessage());
                listenerMap.put(ipAddress, listenerThread);
                listenerThread.start();
                User.getInstance().addAnActiveChannel(ipAddress);
            }

            Platform.runLater(
                    () -> {
                        // Get connected users on channel
                        ArrayList<String> connectedUsers = NetworkControllerAction.getInstance().notifyChannelOfMyConnectionAndGetConnectedUsers(ipAddress);
                        connectedUsersList.getItems().clear();
                        connectedUsersList.getItems().addAll(connectedUsers);
                    }
            );

            User.getInstance().setActiveChannel(ipAddress);

            // Set prompt message
            currentChannel = ipAddress;
            sendingArea.setDisable(false);
            textMessage.setPromptText(String.format(mainMessages.getString("prompt"), currentChannel));
            channelName.setText(currentChannel);
        }
    }

    public void goToPrivateMessages() {
    }

    public void sendMessage() {
        if (!textMessage.getText().isEmpty()) {
            Message message = new TextMessage(User.getInstance().getUsername(), textMessage.getText());
            MessageSenderService messageSender = new MessageSenderService();
            try {
                messageSender.sendMessageOn(currentChannel, MULTICAST_PORT_LISTENER, MessageSenderService.SendingMode.MULTICAST, message);
            } catch (UnsupportedSendingModeException | IOException e) {
                e.printStackTrace();
            }
            textMessage.setText("");
        }
    }
}

