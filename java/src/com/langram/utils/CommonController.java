package com.langram.utils;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class CommonController implements javafx.fxml.Initializable {

    private static ResourceBundle globalMessages = ResourceBundle.getBundle("GlobalMessagesBundle", Settings.getInstance().getLocale());;
    public FontAwesomeIconView maximizeButton;
    public FontAwesomeIconView closeButton;
    public FontAwesomeIconView settingsButton;

    protected final ExecutorService threadsPool = Executors.newCachedThreadPool();
    protected final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public CommonController() {
        // Clean exit thread when application shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            threadsPool.shutdown();
            scheduler.shutdown();
        }));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

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
