package org.tourplanner.view;

import org.springframework.stereotype.Controller;
import org.tourplanner.viewmodel.MainViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import org.tourplanner.viewmodel.TourInputViewModel;
import org.tourplanner.viewmodel.TourLogInputViewModel;

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

    @FXML
    private void onTourReportButtonClick(ActionEvent actionEvent) {
        mainViewModel.createTourReport();
    }

    @FXML
    private void onSummaryReportButtonClick(ActionEvent actionEvent) {
        mainViewModel.createSummaryReport();
    }

    @FXML private void onExportToursClick() {
        mainViewModel.exportTours();
    }

    @FXML private void onImportToursClick() {
        mainViewModel.importTours();
    }
}
