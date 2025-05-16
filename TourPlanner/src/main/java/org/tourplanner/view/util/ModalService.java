package org.tourplanner.view.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.tourplanner.model.ModalType;
import org.tourplanner.view.ModalController;

import java.io.IOException;

public class ModalService {

    public static void showInfoModal(String title, String contentText) {
        try {
            FXMLLoader loader = new FXMLLoader(ModalService.class.getResource("/org/tourplanner/confirmation-modal.fxml"));
            Parent root = loader.load();

            ModalController controller = loader.getController();
            controller.setModalContent(title, contentText);
            controller.showOkOnly(); // show only the OK button

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            controller.setStage(stage); // let the controller close the modal
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showDeleteConfirmation(String header, String content, Runnable onDeleteConfirmed) {
        try {
            FXMLLoader loader = new FXMLLoader(ModalService.class.getResource("/org/tourplanner/confirmation-modal.fxml"));
            Parent root = loader.load();

            ModalController controller = loader.getController();
            controller.configure(header, content, ModalType.DELETE_CONFIRMATION, onDeleteConfirmed);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(header);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
