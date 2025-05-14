package org.tourplanner.view;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import org.tourplanner.model.Tour;
import org.tourplanner.viewmodel.TourListViewModel;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TourListController implements Initializable {

    private final TourListViewModel viewModel;

    public TourListController(TourListViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @FXML
    private VBox tourListContainer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rebuildTourList();

        viewModel.getTours().addListener((ListChangeListener<Tour>) change -> rebuildTourList());
    }

    private void rebuildTourList() {
        tourListContainer.getChildren().clear();

        for (Tour tour : viewModel.getTours()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/tourplanner/tour-list-item.fxml"));
                VBox tourBox = loader.load();

                TourListItemController controller = loader.getController();
                controller.setTour(tour);

                tourListContainer.getChildren().add(tourBox);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
