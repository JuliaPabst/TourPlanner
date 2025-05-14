package org.tourplanner.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.tourplanner.model.Tour;

public class TourListItemController {

    @FXML private Label titleLabel;
    @FXML private Label fromLabel;
    @FXML private Label toLabel;
    @FXML private Label distanceLabel;
    @FXML private Label popularityLabel;

    public void setTour(Tour tour) {
        titleLabel.setText(tour.name());
        fromLabel.setText("From: " + tour.from());
        toLabel.setText("To: " + tour.to());
        distanceLabel.setText(tour.distance() + " km | " + tour.estimatedTime() + " min");
        popularityLabel.setText("Popularity: ★★★★"); // Todo: calculate dynamic popularity
    }
}
