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
import org.tourplanner.view.util.ModalService;
import org.tourplanner.viewmodel.TourInputViewModel;
import org.tourplanner.viewmodel.TourListViewModel;
import org.tourplanner.viewmodel.TourLogInputViewModel;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class TourDetailController implements Initializable {
    @FXML
    public Label routeSectionTitle;
    @FXML
    public Label transportTypeSectionTitle;
    @FXML
    public Label statsSectionTitle;
    @FXML
    public Label metricsSectionTitle;
    @FXML
    public Label mapSectionTitle;
    @FXML
    public Label descriptionSectionTitle;
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
    private final TourLogInputViewModel logInputViewModel;

    public TourDetailController(TourListViewModel listViewModel, TourInputViewModel inputViewModel, TourLogInputViewModel logInputViewModel) {
        this.listViewModel = listViewModel;
        this.inputViewModel = inputViewModel;
        this.logInputViewModel = logInputViewModel;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listViewModel.selectedTourProperty().addListener((obs, oldTour, newTour) -> {
            if (newTour != null) {
                updateView(newTour);
            } else {
                showNoSelectionMessage();
            }
        });

        Tour current = listViewModel.selectedTourProperty().get();
        if (current != null) {
            updateView(current);
        } else {
            showNoSelectionMessage();
        }

        deleteButton.setDisable(false); // optional
    }

    private void showNoSelectionMessage() {
        editButton.setVisible(false);
        deleteButton.setVisible(false);

        titleLabel.setText("Please select a tour from the overview on the left");
        fromLabel.setText("");
        toLabel.setText("");
        transportTypeLabel.setText("");
        distanceLabel.setText("");
        timeLabel.setText("");
        descriptionText.setText("");
        popularityLabel.setText("");
        childFriendlyLabel.setText("");
        mapImageView.setImage(null);
        routeSectionTitle.setText("");
        transportTypeSectionTitle.setText("");
        statsSectionTitle.setText("");
        metricsSectionTitle.setText("");
        mapSectionTitle.setText("");
        descriptionSectionTitle.setText("");
    }

    private void updateView(Tour tour) {
        editButton.setVisible(true);
        deleteButton.setVisible(true);

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

        routeSectionTitle.setText("Route");
        transportTypeSectionTitle.setText("Transport Type");
        statsSectionTitle.setText("Stats");
        metricsSectionTitle.setText("Metrics");
        mapSectionTitle.setText("Map");
        descriptionSectionTitle.setText("Description");

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

    @FXML
    private void onDeleteButtonClicked() {
        Tour selected = listViewModel.selectedTourProperty().get();
        if (selected == null) return;

        ModalService.showDeleteConfirmation(
                "Delete Tour",
                "Are you sure you want to delete \"" + selected.name() + "\"?",
                () -> listViewModel.deleteTour(selected)
        );
    }

    @FXML
    private void onAddLogButtonClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/tourplanner/tour-log-input.fxml"));
            loader.setControllerFactory(clazz -> new TourLogInputController(logInputViewModel));

            Parent root = loader.load();

            Stage dialog = new Stage();
            dialog.setTitle("Add Tour Log");
            dialog.setScene(new Scene(root));
            dialog.initModality(Modality.APPLICATION_MODAL);

            TourLogInputController controller = loader.getController();
            controller.setDialogStage(dialog);

            dialog.showAndWait();

        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}