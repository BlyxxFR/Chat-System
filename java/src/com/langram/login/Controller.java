package com.langram.login;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.langram.utils.CommonController;
import com.langram.utils.Settings;
import com.langram.utils.User;
import com.langram.utils.exchange.network.controller.NetworkController;
import com.langram.utils.messages.ControlMessage;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller extends CommonController implements javafx.fxml.Initializable {

    private ResourceBundle loginMessages = ResourceBundle.getBundle("LoginMessagesBundle", Settings.getInstance().getLocale());
    public JFXTextField usernameText;
    public JFXPasswordField passwordText;
    public Button loginButton;
    public Button registerButton;
    public Label loginStatus;

    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        usernameText.setPromptText(loginMessages.getString("username"));
        passwordText.setPromptText(loginMessages.getString("password"));
        loginButton.setText(loginMessages.getString("login"));
        registerButton.setText(loginMessages.getString("register"));
    }


    public void login() throws Exception {
        loginStatus.setText(loginMessages.getString("loginInProgress"));
        loginStatus.setTextFill(Color.BLACK);

        String[] args = { usernameText.getText() };
        boolean status = NetworkController.getInstance().sendControlMessage(ControlMessage.NetworkControllerMessageType.CheckForUniqueUsername, args);

        if(status) {
            new User(usernameText.getText());
            Login.getInstance().replaceSceneContent("main.fxml", Settings.getInstance().getDefaultWidth(), Settings.getInstance().getDefaultHeight());
        } else {
            loginStatus.setText(loginMessages.getString("usernameAlreadyTaken"));
            loginStatus.setTextFill(Color.RED);
        }
    }
}
