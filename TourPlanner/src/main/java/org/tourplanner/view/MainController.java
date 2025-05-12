package org.tourplanner.view;

import org.tourplanner.model.Route;
import org.tourplanner.service.RouteManager;
import org.tourplanner.viewmodel.MainViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private final MainViewModel viewModel;

    public MainController(MainViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {}

    @FXML
    public void onExitButtonClick(ActionEvent actionEvent) {
        System.exit(0);
    }

    @FXML
    public void onSearch() {
        //TODO: ... left empty for now
        System.out.println("Search clicked (doesn't work yet)");
    }
}
