package com.langram.utils;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public abstract class App extends Application {

    private Stage stage;
    private static App instance;

    // Screen offsets
    private double x = 0;
    private double y = 0;

    public App() {
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
        registerMouseEvents(root);
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
            stage.setX(event.getScreenX() - x);
            stage.setY(event.getScreenY() - y);
        });
    }
}
