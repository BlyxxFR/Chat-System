package com.langram.login;

import com.langram.utils.CommonController;
import com.langram.utils.Settings;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller extends CommonController implements javafx.fxml.Initializable {

    public Label usernameText;
    public Button loginButton;
    public Button registerButton;

    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        ResourceBundle loginMessages = ResourceBundle.getBundle("LoginMessagesBundle", Settings.getLocale());
        //usernameText.setText(loginMessages.getString("projects"));
        loginButton.setText(loginMessages.getString("login"));
        registerButton.setText(loginMessages.getString("register"));
    }


    public void login() throws Exception {
        Login.getInstance().replaceSceneContent("main.fxml", Settings.getDefaultWidth(), Settings.getDefaultHeight());
    }
}
