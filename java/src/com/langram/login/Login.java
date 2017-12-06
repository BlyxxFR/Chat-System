package com.langram.login;

import com.langram.utils.App;
import com.langram.utils.Settings;
import com.langram.utils.channels.Channel;
import com.langram.utils.channels.ChannelRepository;
import com.langram.utils.exchange.database.DatabaseStore;
import javafx.stage.Stage;

import java.util.ResourceBundle;

public class Login extends App {

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage stage) throws Exception {

        super.start(stage, "login.fxml", Settings.getInstance().getLoginWidth(), Settings.getInstance().getLoginHeight());

        /**
         *  -------------- TESTS DB : TO BE REMOVED --------------
         */
        System.out.println(Settings.getInstance().getDatabasePath());

        Channel c = new Channel("Super Channel", "239.0.0.1");
        ChannelRepository cr = new ChannelRepository();
        cr.store(c);

        /**
         * -------------------------------------------------------
         */
        // Get App name and title locale
        ResourceBundle globalMessages = ResourceBundle.getBundle("GlobalMessagesBundle", Settings.getInstance().getLocale());
        stage.setTitle(Settings.getInstance().getAppName() + " - " + globalMessages.getString("titleLoginWindow"));

        // Show the stage
        stage.show();
    }
}

