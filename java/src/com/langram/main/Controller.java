package com.langram.main;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import com.langram.utils.CommonController;
import com.langram.utils.messages.Message;
import com.langram.utils.messages.MessageDisplay;
import com.langram.utils.Settings;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.binding.Bindings;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

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
    private int i = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        // Load labels
        ResourceBundle mainMessages = ResourceBundle.getBundle("MainMessagesBundle", Settings.getInstance().getLocale());
        projectsLabel.setText(mainMessages.getString("projects"));
        onlineLabel.setText(mainMessages.getString("online"));
        displayUsername.setText(Settings.getInstance().getUsername());

        // Set messages' list css
        String css = this.getClass().getResource("/com/langram/utils/resources/messages.css").toExternalForm();
        messagesList.getStylesheets().add(css);
        messagesList.setCellFactory(param -> new MessageDisplay());

        // Set projects' list css
        css = this.getClass().getResource("/com/langram/utils/resources/projects.css").toExternalForm();
        projectsList.getStylesheets().add(css);

        // Load current channel name
        String currentChannel = "myProject";
        channelName.setText(currentChannel);

        // Load project list
        projectsList.getItems().add(messagesList.getItems().size(), currentChannel);

        // Set prompt message
        textMessage.setPromptText(String.format(mainMessages.getString("prompt"), currentChannel));

    }

    public void onReceivedMessage() {
        String c = "test " + i;
        Date date = new Date();
        Message message = new Message("Sender Name", date.toString(), c);

        messagesList.getItems().add(message);
        messagesList.scrollTo(message);

        i++;
    }

    @Override
    public void AddProjectChannel(MouseEvent mouseEvent) {
        this.onReceivedMessage();
    }

}
