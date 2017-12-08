package com.langram.login;

import com.langram.utils.App;
import com.langram.utils.Settings;
import javafx.stage.Stage;

import java.util.ResourceBundle;

public class Login extends App {

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage stage) throws Exception {

        super.start(stage, "login.fxml", Settings.getInstance().getLoginWidth(), Settings.getInstance().getLoginHeight());

        // Get App name and title locale
        ResourceBundle globalMessages = ResourceBundle.getBundle("GlobalMessagesBundle", Settings.getInstance().getLocale());
        stage.setTitle(Settings.getInstance().getAppName() + " - " + globalMessages.getString("titleLoginWindow"));

        // Show the stage
        stage.show();
    }
}

