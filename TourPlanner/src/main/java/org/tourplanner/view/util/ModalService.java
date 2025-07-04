package org.tourplanner.view.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.tourplanner.persistence.entity.ModalType;
import org.tourplanner.view.LoadingModalController;
import org.tourplanner.view.ModalController;
import javafx.application.Platform;

import java.io.IOException;

public class ModalService {
    private static Stage currentLoadingStage;

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

    public static Stage showLoadingModal(String message) {
        try {
            FXMLLoader loader = new FXMLLoader(ModalService.class.getResource("/org/tourplanner/loading-modal.fxml"));
            Parent root = loader.load();

            LoadingModalController controller = loader.getController();

            Stage stage = new Stage();
            controller.setStage(stage);

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Loading...");
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

            currentLoadingStage = stage; // Save reference
            return stage;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load loading modal", e);
        }
    }

    public static void closeAnyLoadingModal() {
        Stage stageToClose = currentLoadingStage;

        if (stageToClose != null) {
            Platform.runLater(() -> {
                if (stageToClose.isShowing()) {
                    stageToClose.close();
                }
            });
            currentLoadingStage = null; // set outside runLater to avoid race conditions
        }
    }
}
