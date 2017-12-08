package com.langram.main;

import com.langram.utils.Settings;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

class MainView {

    static class AddChannelDialog {
        private Dialog<Pair<String, String>> dialog;
        private TextField nameChannel;
        private TextField ipChannel;
        private ButtonType cancelButtonType;
        ButtonType addButtonType;

        AddChannelDialog() {
            Dialog<Pair<String, String>> dialog = new Dialog<>();
            dialog.setTitle(Settings.getInstance().getAppName());

            // Set the button types.
            cancelButtonType = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
            addButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(cancelButtonType, addButtonType);

            GridPane gridPane = new GridPane();
            gridPane.setHgap(10);
            gridPane.setVgap(10);
            gridPane.setPadding(new Insets(20, 150, 10, 10));


            nameChannel = new TextField();
            nameChannel.setPromptText("Nom");
            ipChannel = new TextField();
            ipChannel.setPromptText("IP");

            gridPane.add(nameChannel, 0, 0);
            gridPane.add(ipChannel, 2, 0);
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
