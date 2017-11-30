package com.langram.utils;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class CommonController implements javafx.fxml.Initializable {

    protected static ResourceBundle globalMessages;
    public FontAwesomeIconView maximizeButton;
    public FontAwesomeIconView closeButton;
    public FontAwesomeIconView settingsButton;

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

    public void MaximizeApplication(MouseEvent mouseEvent) {
        boolean status = App.getInstance().maximize();
        maximizeButton.setGlyphName(status ? "WINDOW_MINIMIZE" : "WINDOW_MAXIMIZE");
    }

    public void OpenSettings(MouseEvent mouseEvent) {}


    public void AddProjectChannel(MouseEvent mouseEvent) {}

}
