package com.langram.login;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.langram.utils.CommonController;
import com.langram.utils.Settings;
import com.langram.utils.User;
import com.langram.utils.exchange.network.controller.NetworkControllerAction;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController extends CommonController implements javafx.fxml.Initializable {

    private ResourceBundle loginMessages = ResourceBundle.getBundle("LoginMessagesBundle", Settings.getInstance().getLocale());
    public JFXTextField usernameText;
    public JFXPasswordField passwordText;
    public Button loginButton;
    public Label loginStatus;

    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        usernameText.setPromptText(loginMessages.getString("username"));
        passwordText.setPromptText(loginMessages.getString("password"));
        loginButton.setText(loginMessages.getString("login"));
    }

    public void login() {
        loginStatus.setText(loginMessages.getString("login_in_progress"));
        loginStatus.setTextFill(Color.BLACK);

        new Thread(new Task<Void>() {
            @Override
            protected Void call() {
                Platform.runLater(
                        () -> {
                            if (NetworkControllerAction.getInstance().isAnUsernameAvailable(usernameText.getText())) {
                                new User(usernameText.getText());
                                try {
                                    Login.getInstance().replaceSceneContent("main.fxml", Settings.getInstance().getDefaultWidth(), Settings.getInstance().getDefaultHeight());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                loginStatus.setText(loginMessages.getString("username_already_taken"));
                                loginStatus.setTextFill(Color.RED);
                            }
                        }
                );
                return null;
            }
        }).start();

    }
}
