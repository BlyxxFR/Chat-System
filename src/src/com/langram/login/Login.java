package com.langram.login;

import com.langram.utils.App;
import com.langram.utils.Settings;
import javafx.stage.Stage;

import java.util.ResourceBundle;

public class Login extends App {

    public void start(Stage stage) throws Exception {

        super.start(stage, "login.fxml", Settings.getLoginWidth(), Settings.getLoginHeight());

        // Get App name and title locale
        ResourceBundle globalMessages = ResourceBundle.getBundle("GlobalMessagesBundle", Settings.getLocale());
        stage.setTitle(Settings.getAppName() + " - " + globalMessages.getString("titleLoginWindow"));

        // Show the stage
        stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}

