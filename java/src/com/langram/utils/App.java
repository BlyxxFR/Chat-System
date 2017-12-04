package com.langram.utils;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public abstract class App extends Application {

    private Stage stage;
    private static App instance;
    private static ResourceBundle globalMessages;
    private static List<String> doNotResize = Arrays.asList("login");
    // Screen offsets
    private double x = 0;
    private double y = 0;
    private boolean isMaximized = false;

    public App() {
        globalMessages = ResourceBundle.getBundle("GlobalMessagesBundle", Settings.getInstance().getLocale());
        this.instance = this;
        // Clean exit thread when application shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // what you want to do
            System.out.println("Exit");
        }));
    }
    public static App getInstance() {
        return instance;
    }

    public void replaceSceneContent(String resource, int width, int height) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/com/langram/utils/resources/" + resource));
        Scene scene = stage.getScene();
        if (scene == null) {
            stage.setScene(new Scene(root, width, height));
        } else {
            stage.getScene().setRoot(root);
            stage.setWidth(width);
            stage.setHeight(height);
        }
        stage.setTitle(Settings.getInstance().getAppName() + " - " + globalMessages.getString("titleWindow"));
        if(!this.isMaximized) {
            registerMouseEvents(root);
            if (!doNotResize.contains(resource))
                ResizeHelper.addResizeListener(stage);
        }
        // Set scrollbar css
        String css = this.getClass().getResource("/com/langram/utils/resources/scrollbar.css").toExternalForm();
        assert scene != null;
        scene.getStylesheets().add(css);

    }

    protected void start(Stage stage, String resource, int width, int height) throws Exception{
        // Init view
        Parent root = FXMLLoader.load(getClass().getResource("/com/langram/utils/resources/" + resource));
        // Making the app borderless
        stage.initStyle(StageStyle.UNDECORATED);
        // Loading scene
        stage.setScene(new Scene(root, width, height));
        registerMouseEvents(root);
        this.stage = stage;
    }

    private void registerMouseEvents(Parent root) {
        // Mouse event
        root.setOnMousePressed(event -> {
            x = event.getSceneX();
            y = event.getSceneY();
        });
        root.setOnMouseDragged(event -> {
            if(y > 2 && y < 25 && x > 2 && x < stage.getWidth() - 2) {
                stage.setX(event.getScreenX() - x);
                stage.setY(event.getScreenY() - y);
            }
        });
    }

    boolean maximize() {
        this.isMaximized = !this.isMaximized;
        stage.setMaximized(this.isMaximized);
        stage.setResizable(!this.isMaximized);
        if(this.isMaximized) {
            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX(primaryScreenBounds.getMinX());
            stage.setY(primaryScreenBounds.getMinY());
            stage.setWidth(primaryScreenBounds.getWidth());
            stage.setHeight(primaryScreenBounds.getHeight());
        }
        return this.isMaximized;
    }
}