package com.langram.main;

import com.jfoenix.controls.JFXTextArea;
import com.langram.utils.CommonController;
import com.langram.utils.Settings;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller extends CommonController implements javafx.fxml.Initializable {

    public Label projectsText;
    public JFXTextArea textMessage;
    public FontAwesomeIconView sendFile;
    public FontAwesomeIconView sendMessage;
    public ImageView profileImage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        ResourceBundle mainMessages = ResourceBundle.getBundle("MainMessagesBundle", Settings.getLocale());
        projectsText.setText(mainMessages.getString("projects"));

    }

}
