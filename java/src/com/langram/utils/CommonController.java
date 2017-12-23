package com.langram.utils;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class CommonController implements javafx.fxml.Initializable {

    public FontAwesomeIconView maximizeButton;
    public FontAwesomeIconView closeButton;

    protected final ExecutorService threadsPool = Executors.newCachedThreadPool();
    protected final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

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

    public void closeApplication() {
        System.exit(0);
    }

    public void maximizeApplication() {
        boolean status = App.getInstance().maximize();
        maximizeButton.setGlyphName(status ? "WINDOW_MINIMIZE" : "WINDOW_MAXIMIZE");
    }

    public void displayTray(String text) {
        try {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("/com/langram/utils/resources/images/logo-mini.png"));
            TrayIcon trayIcon = new TrayIcon(image, Settings.getInstance().getAppName());
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip(Settings.getInstance().getAppName());
            tray.add(trayIcon);
            trayIcon.displayMessage(Settings.getInstance().getAppName(), text, TrayIcon.MessageType.INFO);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

}
