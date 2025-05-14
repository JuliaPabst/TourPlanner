package org.tourplanner.view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import org.tourplanner.model.Tour;
import org.tourplanner.viewmodel.TourListViewModel;

import java.net.URL;
import java.util.ResourceBundle;

public class TourListController implements Initializable {
    private final TourListViewModel viewModel;

    public TourListController(TourListViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @FXML
    private ListView<Tour> tourList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tourList.setItems(viewModel.getTours());
    }
}
