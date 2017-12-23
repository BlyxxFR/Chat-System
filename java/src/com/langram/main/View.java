package com.langram.main;

import com.langram.utils.Settings;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.StageStyle;
import javafx.util.Pair;

import java.util.ResourceBundle;

class View {

    static class AddChannelDialog {
        private Dialog<Pair<String, String>> dialog;
        private TextField nameChannel;
        private TextField ipChannel;
        private ButtonType cancelButtonType;
        ButtonType addButtonType;

        ResourceBundle mainMessages = ResourceBundle.getBundle("MainMessagesBundle", Settings.getInstance().getLocale());

        AddChannelDialog() {
            Dialog<Pair<String, String>> dialog = new Dialog<>();
            dialog.initStyle(StageStyle.UNDECORATED);
            dialog.setTitle(Settings.getInstance().getAppName());

            // Set the button types.
            cancelButtonType = new ButtonType(mainMessages.getString("cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
            addButtonType = new ButtonType(mainMessages.getString("submit"), ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(cancelButtonType, addButtonType);

            GridPane gridPane = new GridPane();
            gridPane.setHgap(10);
            gridPane.setVgap(10);
            gridPane.setPadding(new Insets(10, 10, 10, 10));

            nameChannel = new TextField();
            nameChannel.setPromptText(mainMessages.getString("channel_name"));
            ipChannel = new TextField();
            ipChannel.setPromptText(mainMessages.getString("channel_address"));

            gridPane.add(new Label(mainMessages.getString("channel_name")), 0, 0);
            gridPane.add(new Label(mainMessages.getString("channel_address")), 0, 1);

            gridPane.add(nameChannel, 1, 0);
            gridPane.add(ipChannel, 1, 1);
            dialog.getDialogPane().setContent(gridPane);
            this.dialog = dialog;
        }

        Dialog<Pair<String, String>> get() {
            return this.dialog;
        }

        TextField getNameField() {
            return this.nameChannel;
        }

        TextField getIpField() {
            return this.ipChannel;
        }

        ButtonType getAddButton() {
            return this.addButtonType;
        }
    }

}
