package com.langram.main;

import com.langram.utils.CommonController;
import com.langram.utils.Settings;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller extends CommonController implements javafx.fxml.Initializable {

    public Label projectsText;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        ResourceBundle mainMessages = ResourceBundle.getBundle("MainMessagesBundle", Settings.getLocale());
        projectsText.setText(mainMessages.getString("projects"));
    }

}
