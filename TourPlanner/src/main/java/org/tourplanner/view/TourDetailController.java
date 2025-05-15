package org.tourplanner.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.tourplanner.model.Tour;
import org.tourplanner.viewmodel.TourInputViewModel;
import org.tourplanner.viewmodel.TourListViewModel;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
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
    private Label descriptionText;

    @FXML
    private Button editButton;

    @FXML
    private Button deleteButton;

    private final TourListViewModel listViewModel;
    private final TourInputViewModel inputViewModel;

    public TourDetailController(TourListViewModel listViewModel, TourInputViewModel inputViewModel) {
        this.listViewModel = listViewModel;
        this.inputViewModel = inputViewModel;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listViewModel.selectedTourProperty().addListener((obs, oldTour, newTour) -> {
            if (newTour != null) {
                updateView(newTour);
            }
        });

        Tour current = listViewModel.selectedTourProperty().get();
        if (current != null) {
            updateView(current);
        }

        // Todo: button actions
        deleteButton.setDisable(true);
    }

    private void updateView(Tour tour) {
        titleLabel.setText(tour.name());
        fromLabel.setText("From: " + tour.from());
        toLabel.setText("To: " + tour.to());
        transportTypeLabel.setText(tour.transportType().name());
        distanceLabel.setText("Distance: " + tour.distance() + " km");
        timeLabel.setText("Est. time: " + tour.estimatedTime() + " min");
        descriptionText.setText(tour.tourDescription());

        // Placeholder values
        popularityLabel.setText("Popularity: ★★★★");
        childFriendlyLabel.setText("Child-friendly: ★★★");

        // TODO: Load actual image
        mapImageView.setImage(new Image(Objects.requireNonNull(getClass().getResource("/testImage.jpeg")).toExternalForm()));
    }

    @FXML
    private void onEditButtonClicked() {
        Tour selected = listViewModel.selectedTourProperty().get();
        if (selected == null) return;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/tourplanner/tour-input.fxml"));
            loader.setControllerFactory(clazz -> new TourInputController(inputViewModel));

            Parent root = loader.load();

            inputViewModel.startEditing(selected); // prefill with selected tour

            Stage dialog = new Stage();
            dialog.setScene(new Scene(root));
            dialog.setTitle("Edit Tour");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}