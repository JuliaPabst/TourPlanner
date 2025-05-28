package org.tourplanner.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.springframework.stereotype.Controller;
import org.tourplanner.persistence.entity.Tour;
import org.tourplanner.viewmodel.TourListViewModel;

@Controller
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

        titleLabel.setText(tour.getTourName());
        fromLabel.setText("From: " + tour.getFrom());
        toLabel.setText("To: " + tour.getTo());
        distanceLabel.setText(tour.getDistance() + " km | " + tour.getEstimatedTime() + " min");
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
