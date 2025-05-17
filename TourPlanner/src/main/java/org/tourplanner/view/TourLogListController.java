package org.tourplanner.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.collections.ListChangeListener;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.tourplanner.model.TourLog;
import org.tourplanner.viewmodel.TourLogInputViewModel;
import org.tourplanner.viewmodel.TourLogListViewModel;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TourLogListController implements Initializable {
    private final TourLogListViewModel viewModel;
    private final TourLogInputViewModel inputViewModel;

    public TourLogListController(TourLogListViewModel viewModel, TourLogInputViewModel inputViewModel) {
        this.viewModel = viewModel;
        this.inputViewModel = inputViewModel;
    }

    @FXML
    private VBox logListContainer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        viewModel.getLogs().addListener((ListChangeListener<TourLog>) change -> rebuildLogList());
        rebuildLogList();
    }

    private void rebuildLogList() {
        logListContainer.getChildren().clear();

        for(TourLog log : viewModel.getLogs()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/tourplanner/tour-log-list-item.fxml"));
                VBox logBox = loader.load();

                TourLogListItemController controller = loader.getController();
                controller.setLog(log);
                controller.setViewModel(inputViewModel);

                logListContainer.getChildren().add(logBox);
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void onAddLogClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/tourplanner/tour-log-input.fxml"));
            loader.setControllerFactory(clazz -> new TourLogInputController(inputViewModel));

            Parent root = loader.load();

            Stage dialog = new Stage();
            dialog.setTitle("Add Tour Log");
            dialog.setScene(new Scene(root));
            dialog.initModality(Modality.APPLICATION_MODAL);

            TourLogInputController controller = loader.getController();
            controller.setDialogStage(dialog);

            dialog.showAndWait();

        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}