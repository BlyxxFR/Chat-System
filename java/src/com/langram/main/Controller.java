package com.langram.main;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import com.langram.utils.CommonController;
import com.langram.utils.Settings;
import com.langram.utils.exchange.IPAddressValidator;
import com.langram.utils.exchange.IncomingMessageListener;
import com.langram.utils.exchange.MessageReceiverThread;
import com.langram.utils.exchange.MessageSenderService;
import com.langram.utils.exchange.exception.UnsupportedSendingModeException;
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
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.langram.utils.exchange.MessageSenderService.SendingMode.MULTICAST;

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
    public Label privateMessageLabel;
    public FontAwesomeIconView privateMessagesEnvelope;
    public AnchorPane sendingArea;

    private String currentChannel = null;
    private ResourceBundle mainMessages;
    private HashMap<String, MessageReceiverThread> listenerMap = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        // Load labels
        mainMessages = ResourceBundle.getBundle("MainMessagesBundle", Settings.getInstance().getLocale());
        projectsLabel.setText(mainMessages.getString("projects"));
        onlineLabel.setText(mainMessages.getString("online"));
        displayUsername.setText(Settings.getInstance().getUsername());

        // Set private message label
        int privateMessageCounter = 3;
        privateMessageLabel.setText(String.format(mainMessages.getString(privateMessageCounter > 1 ? "new_message_plural" : "new_message"), privateMessageCounter));
        privateMessagesEnvelope.setGlyphName(privateMessageCounter > 0 ? "ENVELOPE_ALT" : "ENVELOPE_OPEN_ALT");

        // Set messages' list css
        String css = this.getClass().getResource("/com/langram/utils/resources/messages.css").toExternalForm();
        messagesList.getStylesheets().add(css);
        messagesList.setCellFactory(param -> new MessageDisplay());

        // Set projects' list css and click action
        css = this.getClass().getResource("/com/langram/utils/resources/projects.css").toExternalForm();
        projectsList.getStylesheets().add(css);

        // Disable areas in case of no channel is added
        sendingArea.setDisable(true);

        // Load project list
        //projectsList.getItems().add(messagesList.getItems().size(), currentChannel);

    }

    public class onReceivedMessage implements IncomingMessageListener {
        public void onNewIncomingMessage(final Message message) {
            Platform.runLater(
                    () -> {
                        messagesList.getItems().add(message);
                        messagesList.scrollTo(message);
                    }
            );
        }
    }

    public class onReceivedPrivateMessage implements IncomingMessageListener {
        public void onNewIncomingMessage(final Message message) {
            Platform.runLater(
                    () -> {

                    }
            );
        }
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
        if(ipAddressValidator.validate(entered)) { projectsList.getItems().add(entered); }
    }

    public void goToChannel() {
        String ipAddress = projectsList.getSelectionModel().getSelectedItems().get(0);
        if (ipAddress != null) {
            if (!listenerMap.containsKey(ipAddress)) {
                // Start thread listener
                MessageReceiverThread listenerThread = new MessageReceiverThread(MULTICAST, ipAddress, 4488, new onReceivedMessage());
                listenerMap.put(ipAddress, listenerThread);
                listenerThread.start();
            }

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
        Message message = new TextMessage(Settings.getInstance().getUsername(), textMessage.getText());
        MessageSenderService messageSender = new MessageSenderService();
        try {
            messageSender.sendMessageOn(currentChannel, 4488, MessageSenderService.SendingMode.MULTICAST, message);
        } catch (UnsupportedSendingModeException | IOException e) {
            e.printStackTrace();
        }
        textMessage.setText("");
    }
}

