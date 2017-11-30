package com.langram.login;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.langram.utils.CommonController;
import com.langram.utils.Settings;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller extends CommonController implements javafx.fxml.Initializable {

    public JFXTextField usernameText;
    public JFXPasswordField passwordText;
    public Button loginButton;
    public Button registerButton;

    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        ResourceBundle loginMessages = ResourceBundle.getBundle("LoginMessagesBundle", Settings.getLocale());
        usernameText.setPromptText(loginMessages.getString("username"));
        passwordText.setPromptText(loginMessages.getString("password"));
        loginButton.setText(loginMessages.getString("login"));
        registerButton.setText(loginMessages.getString("register"));
    }


    public void login() throws Exception {
        Login.getInstance().replaceSceneContent("main.fxml", Settings.getDefaultWidth(), Settings.getDefaultHeight());
    }
}
