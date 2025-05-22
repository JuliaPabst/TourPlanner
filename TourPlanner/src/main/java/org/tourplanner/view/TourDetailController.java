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
import javafx.collections.ListChangeListener;
import org.tourplanner.model.Tour;
import org.tourplanner.model.TourLog;
import org.tourplanner.service.TourLogManager;
import org.tourplanner.view.util.ModalService;
import org.tourplanner.viewmodel.TourInputViewModel;
import org.tourplanner.viewmodel.TourListViewModel;

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
    private final TourLogManager logManager;

    public TourDetailController(TourListViewModel listViewModel, TourInputViewModel inputViewModel, TourLogManager logManager) {
        this.listViewModel = listViewModel;
        this.inputViewModel = inputViewModel;
        this.logManager = logManager;
    }

    private void bindDetailLabels() {
        fromLabel.textProperty().bind(listViewModel.fromLabelProperty());
        toLabel.textProperty().bind(listViewModel.toLabelProperty());
        transportTypeLabel.textProperty().bind(listViewModel.transportTypeLabelProperty());
        distanceLabel.textProperty().bind(listViewModel.distanceLabelProperty());
        timeLabel.textProperty().bind(listViewModel.timeLabelProperty());
        descriptionText.textProperty().bind(listViewModel.descriptionTextProperty());
        popularityLabel.textProperty().bind(listViewModel.popularityTextProperty());
        childFriendlyLabel.textProperty().bind(listViewModel.childFriendlyTextProperty());
    }

    private void unbindDetailLabels() {
        fromLabel.textProperty().unbind();
        toLabel.textProperty().unbind();
        transportTypeLabel.textProperty().unbind();
        distanceLabel.textProperty().unbind();
        timeLabel.textProperty().unbind();
        descriptionText.textProperty().unbind();
        popularityLabel.textProperty().unbind();
        childFriendlyLabel.textProperty().unbind();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bindDetailLabels();

        // React to selection changes
        listViewModel.selectedTourProperty().addListener((obs, oldTour, newTour) -> {
            if(newTour != null) {
                listViewModel.showNoSelectionMessageProperty().set(false);
                updateView(newTour);
            } else {
                listViewModel.showNoSelectionMessageProperty().set(true);
            }
        });

        // Listen for changes in logs that may require an UI update
        logManager.getLogList().addListener((ListChangeListener<TourLog>) change -> {
            Tour current = listViewModel.selectedTourProperty().get();
            if(current != null) {
                updateView(current);
            }
        });

        // Bind visibility and messages to boolean property
        listViewModel.showNoSelectionMessageProperty().addListener((obs, wasVisible, isVisible) -> {
            if(isVisible) {
                showNoSelectionMessage();
            }
        });

        // Initial state
        Tour current = listViewModel.selectedTourProperty().get();
        if(current != null) {
            listViewModel.showNoSelectionMessageProperty().set(false);
            updateView(current);
        } else {
            listViewModel.showNoSelectionMessageProperty().set(true);
        }

        deleteButton.setDisable(false); // optional
    }

    private void showNoSelectionMessage() {
        editButton.setVisible(false);
        deleteButton.setVisible(false);

        titleLabel.setText("Please select a tour from the overview on the left");
        unbindDetailLabels();

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
        bindDetailLabels();
        listViewModel.updateDisplayData(tour);

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
}