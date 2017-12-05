package com.langram.utils.messages;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;


public class MessageDisplay extends ListCell<Message> {

    private final Label textLabel = new Label();
    {
        textLabel.setWrapText(true);
        textLabel.maxWidthProperty().bind(Bindings.createDoubleBinding(
                () -> getWidth() - getPadding().getLeft() - getPadding().getRight(),
                widthProperty(), paddingProperty()));
    }

    @Override
    public void updateItem(Message item, boolean empty) {
        super.updateItem(item, empty);

        if(item != null && !empty) {
            HBox container = new HBox();
            HBox.setHgrow(container, Priority.ALWAYS);

            // messageContainer
            BorderPane messageContainer = new BorderPane();

            // Header
            HBox header = new HBox();

            // Sender and date
            Label senderLabel = new Label(item.getSenderName());
            Label dateLabel = new Label(item.getDate());

            // Sender picture
            ImageView senderPicture = new ImageView();
            senderPicture.setFitHeight(16);
            senderPicture.setFitWidth(16);
            senderPicture.setImage(new Image("/com/langram/utils/resources/images/default-user.png"));

            // Add nodes to header
            HBox.setMargin(senderPicture, new Insets(4.0, 0.0, 0.0, 0.0));
            HBox.setMargin(senderLabel, new Insets(0.0, 10.0, 0.0, 10.0));
            header.getChildren().addAll(senderPicture, senderLabel, dateLabel);
            messageContainer.setTop(header);

            // Set message content
            textLabel.setText(item.getText());
            messageContainer.setBottom(textLabel);

            // Container
            container.getChildren().add(messageContainer);

            setGraphic(container);

        }
    }
}
