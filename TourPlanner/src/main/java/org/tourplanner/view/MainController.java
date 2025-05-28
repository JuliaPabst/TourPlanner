package org.tourplanner.view;

import javafx.scene.Parent;
import org.springframework.stereotype.Controller;
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
import org.tourplanner.viewmodel.TourLogInputViewModel;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class MainController implements Initializable {
    private final MainViewModel mainViewModel;
    private final TourInputViewModel tourInputViewModel;
    private final TourLogInputViewModel tourLogInputViewModel;

    public MainController(MainViewModel mainViewModel, TourInputViewModel tourInputViewModel, TourLogInputViewModel tourLogInputViewModel) {
        this.mainViewModel = mainViewModel;
        this.tourInputViewModel = tourInputViewModel;
        this.tourLogInputViewModel = tourLogInputViewModel;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {}

    @FXML
    public void onExitButtonClick(ActionEvent actionEvent) {
        System.exit(0);
    }
}
