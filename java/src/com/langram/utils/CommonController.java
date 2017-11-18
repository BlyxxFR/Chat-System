package com.langram.utils;

import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class CommonController implements javafx.fxml.Initializable {

    protected static ResourceBundle globalMessages;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Settings.init();
        globalMessages = ResourceBundle.getBundle("GlobalMessagesBundle", Settings.getLocale());
    }

    public static ResourceBundle getGlobalMessagesBundle() {
        return globalMessages;
    }

    public void CloseApplication(MouseEvent mouseEvent) {
        System.exit(0);
    }

    public void OpenSettings(MouseEvent mouseEvent) {}


}
