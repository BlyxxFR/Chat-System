package com.langram.utils;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;

import java.net.URL;
import java.util.ResourceBundle;

public class CommonController implements javafx.fxml.Initializable {

    private static ResourceBundle globalMessages;
    public FontAwesomeIconView maximizeButton;
    public FontAwesomeIconView closeButton;
    public FontAwesomeIconView settingsButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        globalMessages = ResourceBundle.getBundle("GlobalMessagesBundle", Settings.getInstance().getLocale());
    }

    public static ResourceBundle getGlobalMessagesBundle() {
        return globalMessages;
    }

    public void closeApplication() {
        System.exit(0);
    }

    public void maximizeApplication() {
        boolean status = App.getInstance().maximize();
        maximizeButton.setGlyphName(status ? "WINDOW_MINIMIZE" : "WINDOW_MAXIMIZE");
    }

    public void openSettings() {}

}
