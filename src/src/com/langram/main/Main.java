package com.langram.main;

import com.langram.utils.Settings;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ResourceBundle;

public class Main extends Application {

    // Screen offsets
    private double x = 0;
    private double y = 0;

    @Override
    public void start(Stage primaryStage) throws Exception{
        // Init settings
        Settings.init();
        ResourceBundle globalMessages = ResourceBundle.getBundle("GlobalMessagesBundle", Settings.getLocale());

        // Init view
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        // Get App name and title locale
        primaryStage.setTitle(Settings.getAppName() + " - " + globalMessages.getString("titleWindow"));
        // Making the app borderless
        primaryStage.initStyle(StageStyle.UNDECORATED);
        // Loading scene
        primaryStage.setScene(new Scene(root, Settings.getDefaultWidth(), Settings.getDefaultHeight()));
        // Mouse event
        root.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                x = event.getSceneX();
                y = event.getSceneY();
            }
        });
        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                primaryStage.setX(event.getScreenX() - x);
                primaryStage.setY(event.getScreenY() - y);
            }
        });
        // Show the stage
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
