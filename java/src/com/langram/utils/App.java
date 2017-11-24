package com.langram.utils;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

    public App() {
        Settings.init();
        globalMessages = ResourceBundle.getBundle("GlobalMessagesBundle", Settings.getLocale());
        instance = this;
    }
    public static App getInstance() {
        return instance;
    }

    public Parent replaceSceneContent(String resource, int width, int height) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/com/langram/utils/resources/" + resource));
        Scene scene = stage.getScene();
        if (scene == null) {
            stage.setScene(new Scene(root, width, height));
        } else {
            stage.getScene().setRoot(root);
            stage.setWidth(width);
            stage.setHeight(height);
        }
        stage.setTitle(Settings.getAppName() + " - " + globalMessages.getString("titleWindow"));
        registerMouseEvents(root);
        if(!doNotResize.contains(resource)) {
            ResizeHelper.addResizeListener(stage);

        }
        return root;
    }

    public void start(Stage stage, String resource, int width, int height) throws Exception{
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
}
