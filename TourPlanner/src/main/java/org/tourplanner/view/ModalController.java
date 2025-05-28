package org.tourplanner.view;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.stereotype.Controller;
import org.tourplanner.persistence.entity.ModalType;

@Controller
public class ModalController {

    @FXML private Button cancelButton;
    @FXML private Button okButton;
    @FXML private Button deleteButton;
    @FXML private Label headerLabel;
    @FXML private Label contentLabel;

    private ModalType type;
    private Runnable onDeleteConfirmed;

    public void configure(String header, String content, ModalType type, Runnable onDeleteConfirmed) {
        this.type = type;
        this.onDeleteConfirmed = onDeleteConfirmed;

        headerLabel.setText(header);
        contentLabel.setText(content);

        switch (type) {
            case INFO -> {
                okButton.setVisible(true);
                cancelButton.setVisible(false);
                deleteButton.setVisible(false);
            }
            case DELETE_CONFIRMATION -> {
                okButton.setVisible(false);
                cancelButton.setVisible(true);
                deleteButton.setVisible(true);
            }
        }
    }

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setModalContent(String title, String contentText) {
        headerLabel.setText(title);
        contentLabel.setText(contentText);
    }

    public void showOkOnly() {
        okButton.setVisible(true);
        cancelButton.setVisible(false);
        deleteButton.setVisible(false);
    }

    @FXML
    private void onOk() {
        if (stage != null) stage.close();
    }

    @FXML
    private void onCancel() {
        ((Stage) cancelButton.getScene().getWindow()).close();
    }

    @FXML
    private void onConfirmDelete() {
        if (onDeleteConfirmed != null) {
            onDeleteConfirmed.run();
        }
        ((Stage) deleteButton.getScene().getWindow()).close();
    }
}

