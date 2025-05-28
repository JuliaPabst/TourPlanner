package org.tourplanner.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.springframework.stereotype.Controller;
import org.tourplanner.persistence.entity.TourLog;
import org.tourplanner.viewmodel.TourLogInputViewModel;
import org.tourplanner.view.util.ModalService;

@Controller
public class TourLogListItemController {
    @FXML private Label logHeaderLabel;
    @FXML private Label logDetailsLabel;
    @FXML private Label commentLabel;
    @FXML private Button editButton;
    @FXML private Button deleteButton;

    private TourLog log;
    private TourLogInputViewModel logInputViewModel;

    public void setLog(TourLog log) {
        this.log = log;
        logHeaderLabel.setText(log.getDate() + " - " + log.getUsername());
        logDetailsLabel.setText("Time: " + log.getTotalTime() + " min | Distance: " + log.getTotalDistance() + " km | Difficulty: " + log.getDifficulty() + " | Rating: " + log.getRating());
        commentLabel.setText("Comment: " + log.getComment());
    }

    public void setViewModel(TourLogInputViewModel viewModel) {
        this.logInputViewModel = viewModel;
    }

    @FXML
    public void onEditClicked() {
        try {
            logInputViewModel.startEditing(log);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/tourplanner/tour-log-input.fxml"));
            loader.setControllerFactory(clazz -> new TourLogInputController(logInputViewModel));

            Parent root = loader.load();

            Stage dialog = new Stage();
            dialog.setTitle("Edit Tour Log");
            dialog.setScene(new Scene(root));
            dialog.initModality(Modality.APPLICATION_MODAL);

            TourLogInputController controller = loader.getController();
            controller.setDialogStage(dialog);

            dialog.showAndWait();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onDeleteClicked() {
        ModalService.showDeleteConfirmation(
                "Delete Tour Log",
                "Are you sure you want to delete this log entry from " + log.getDate() + "?",
                () -> logInputViewModel.getLogListViewModel().deleteLog(log)
        );
    }
}