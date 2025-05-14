package org.tourplanner.view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.tourplanner.model.Tour;

import java.net.URL;
import java.util.ResourceBundle;

public class TourDetailController implements Initializable {

    @FXML
    private Label titleLabel;
    @FXML
    private Label fromLabel;
    @FXML
    private Label toLabel;
    @FXML
    private Label transportTypeLabel;
    @FXML
    private Label distanceLabel;
    @FXML
    private Label timeLabel;
    @FXML
    private Label popularityLabel;
    @FXML
    private Label childFriendlyLabel;
    @FXML
    private ImageView mapImageView;
    @FXML
    private TextArea descriptionText;

    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;

    private Tour selectedTour;

    public void setTour(Tour tour) {
        this.selectedTour = tour;
        updateView();
    }

    private void updateView() {
        if (selectedTour == null) return;

        titleLabel.setText(selectedTour.name());
        fromLabel.setText("From: " + selectedTour.from());
        toLabel.setText("To: " + selectedTour.to());
        transportTypeLabel.setText(selectedTour.transportType().name());
        distanceLabel.setText("Distance: " + selectedTour.distance() + " km");
        timeLabel.setText("Est. time: " + selectedTour.estimatedTime() + " min");
        descriptionText.setText(selectedTour.tourDescription());

        // Todo: calculate dynamic values
        popularityLabel.setText("Popularity: ★★★★");
        childFriendlyLabel.setText("Child-friendly: ★★★");

        // todo: add logic to add real picture
        mapImageView.setImage(new Image("/placeholder-map.png"));

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Todo: button actions
        editButton.setDisable(true);
        deleteButton.setDisable(true);
    }
}