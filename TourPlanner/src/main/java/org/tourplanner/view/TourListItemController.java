package org.tourplanner.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.tourplanner.model.Tour;
import org.tourplanner.viewmodel.TourListViewModel;

public class TourListItemController {

    @FXML private Label titleLabel;
    @FXML private Label fromLabel;
    @FXML private Label toLabel;
    @FXML private Label distanceLabel;
    @FXML private Label popularityLabel;

    private Tour tour;
    private TourListViewModel viewModel;

    public void setTour(Tour tour) {
        this.tour = tour;
        updateView();
    }

    public void setViewModel(TourListViewModel viewModel) {
        this.viewModel = viewModel;
        updateView();
    }

    public void updateView() {
        if(tour == null) return;

        titleLabel.setText(tour.name());
        fromLabel.setText("From: " + tour.from());
        toLabel.setText("To: " + tour.to());
        distanceLabel.setText(tour.distance() + " km | " + tour.estimatedTime() + " min");
        if(viewModel != null) {
            int popularity = viewModel.getPopularity(tour);
            popularityLabel.setText("Popularity: " + "★".repeat(popularity));
        } else {
            popularityLabel.setText("Popularity: N/A");
        }
    }

    @FXML
    public void onClick() {
        if (viewModel != null && tour != null) {
            viewModel.selectTour(tour);
        }
    }
}
