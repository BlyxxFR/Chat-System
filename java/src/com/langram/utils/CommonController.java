package com.langram.utils;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;

import java.net.URL;
import java.util.ResourceBundle;

public class CommonController implements javafx.fxml.Initializable {

    public FontAwesomeIconView maximizeButton;
    public FontAwesomeIconView closeButton;
    public FontAwesomeIconView settingsButton;
    private static ResourceBundle globalMessagesBundle = ResourceBundle.getBundle("GlobalMessagesBundle", Settings.getLocale());

    static ResourceBundle getGlobalMessagesBundle() {
        return globalMessagesBundle;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Settings.init();
    }

    public void CloseApplication() {
        System.exit(0);
    }

    public void MaximizeApplication() {
        boolean status = App.getInstance().maximize();
        maximizeButton.setGlyphName(status ? "WINDOW_MINIMIZE" : "WINDOW_MAXIMIZE");
    }

    public void OpenSettings() {
    }


}
