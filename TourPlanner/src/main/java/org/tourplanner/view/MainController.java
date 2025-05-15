package org.tourplanner.view;

import org.tourplanner.service.TourManager;
import org.tourplanner.viewmodel.MainViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.tourplanner.viewmodel.TourInputViewModel;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private final MainViewModel mainViewModel;
    private final TourInputViewModel tourInputViewModel;

    public MainController(MainViewModel mainViewModel, TourInputViewModel tourInputViewModel) {
        this.mainViewModel = mainViewModel;
        this.tourInputViewModel = tourInputViewModel;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {}

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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void onExitButtonClick(ActionEvent actionEvent) {
        System.exit(0);
    }
}
