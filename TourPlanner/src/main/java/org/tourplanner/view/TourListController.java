package org.tourplanner.view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.stereotype.Controller;
import org.tourplanner.persistence.entity.Tour;
import org.tourplanner.persistence.entity.TourLog;
import org.tourplanner.service.TourLogManager;
import org.tourplanner.viewmodel.TourInputViewModel;
import org.tourplanner.viewmodel.TourListViewModel;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class TourListController implements Initializable {
    private final TourInputViewModel tourInputViewModel;
    private final TourListViewModel viewModel;

    public TourListController(TourInputViewModel tourInputViewModel, TourListViewModel viewModel) {
        this.tourInputViewModel = tourInputViewModel;
        this.viewModel = viewModel;
    }

    @FXML
    private VBox tourListContainer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rebuildTourList();

        // Refresh when tour list changes
        viewModel.getTours().addListener((ListChangeListener<Tour>) change -> rebuildTourList());
        // Refresh when popularity / child‑friendliness migh have changed due to log updates
        viewModel.refreshTokenProperty().addListener((obs, oldValue, newValue) -> rebuildTourList());
    }

    private void rebuildTourList() {
        tourListContainer.getChildren().clear();

        for (Tour tour : viewModel.getTours()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/tourplanner/tour-list-item.fxml"));
                VBox tourBox = loader.load();

                TourListItemController controller = loader.getController();
                controller.setTour(tour);
                controller.setViewModel(viewModel);

                tourListContainer.getChildren().add(tourBox);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void onOpenAddRouteDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/tourplanner/tour-input.fxml"));
            loader.setControllerFactory(clazz -> new TourInputController(tourInputViewModel));

            Scene scene = new Scene(loader.load());

            // Access controller to inject dialog stage
            TourInputController controller = loader.getController();
            Stage dialogStage = new Stage();
            controller.setDialogStage(dialogStage);

            dialogStage.setTitle("Add New Tour");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(scene);
            dialogStage.showAndWait();

        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
