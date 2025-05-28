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
    private final TourLogManager logManager;

    public TourListController(TourInputViewModel tourInputViewModel, TourListViewModel viewModel, TourLogManager logManager) {
        this.tourInputViewModel = tourInputViewModel;
        this.viewModel = viewModel;
        this.logManager = logManager;
    }

    @FXML
    private VBox tourListContainer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rebuildTourList();

        viewModel.getTours().addListener((ListChangeListener<Tour>) change -> rebuildTourList());
        logManager.getLogList().addListener((ListChangeListener<TourLog>) change -> rebuildTourList());

        viewModel.selectedTourProperty().addListener(new ChangeListener<Tour>() {
            @Override
            public void changed(ObservableValue<? extends Tour> observable, Tour oldValue, Tour newValue) {
                System.out.println("Selected Tour: " + newValue);
            }
        });
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
