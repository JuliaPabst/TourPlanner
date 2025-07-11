package org.tourplanner.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.stereotype.Controller;
import org.tourplanner.persistence.entity.Tour;
import org.tourplanner.view.util.ModalService;
import org.tourplanner.viewmodel.TourInputViewModel;
import org.tourplanner.viewmodel.TourListViewModel;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Controller
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
    private WebView mapWebView;
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

    private void bindDetailLabels() {
        fromLabel.textProperty().bind(listViewModel.fromLabelProperty());
        toLabel.textProperty().bind(listViewModel.toLabelProperty());
        transportTypeLabel.textProperty().bind(listViewModel.transportTypeLabelProperty());
        distanceLabel.textProperty().bind(listViewModel.distanceLabelProperty());
        timeLabel.textProperty().bind(listViewModel.timeLabelProperty());
        descriptionText.textProperty().bind(listViewModel.descriptionTextProperty());
        popularityLabel.textProperty().bind(listViewModel.popularityTextProperty());
        childFriendlyLabel.textProperty().bind(listViewModel.childFriendlyTextProperty());

        listViewModel.mapHtmlContentProperty().addListener((obs, oldHtml, newHtml) -> {
            mapWebView.getEngine().loadContent(newHtml, "text/html");
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bindDetailLabels();

        showNoSelectionMessage();
        listViewModel.showNoSelectionMessageProperty().set(true);

        // React to selection changes
        listViewModel.selectedTourProperty().addListener((obs, oldTour, newTour) -> {
            if(newTour != null) {
                listViewModel.showNoSelectionMessageProperty().set(false);
                listViewModel.updateDisplayData(newTour);
                updateUiVisibility(true, newTour.getTourName());
            } else {
                listViewModel.showNoSelectionMessageProperty().set(true);
                showNoSelectionMessage();
            }
        });

        // Refresh metrics when the underlying logs change
        listViewModel.refreshTokenProperty().addListener((obs, o, n) -> {
            Tour current = listViewModel.getSelectedTour();
            if(current != null) {
                listViewModel.updateDisplayData(current);
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
            listViewModel.updateDisplayData(current);
            updateUiVisibility(true, current.getTourName());
        } else {
            listViewModel.showNoSelectionMessageProperty().set(true);
            showNoSelectionMessage();
        }

        deleteButton.setDisable(false); // optional
    }

    private void updateUiVisibility(boolean visible, String tourTitle) {
        editButton.setVisible(visible);
        deleteButton.setVisible(visible);
        titleLabel.setText(tourTitle);

        routeSectionTitle.setText("Route");
        transportTypeSectionTitle.setText("Transport Type");
        statsSectionTitle.setText("Stats");
        metricsSectionTitle.setText("Metrics");
        mapSectionTitle.setText("Map");
        descriptionSectionTitle.setText("Description");
    }

    private void showNoSelectionMessage() {
        editButton.setVisible(false);
        deleteButton.setVisible(false);

        titleLabel.setText("Please select a tour from the overview on the left");
        listViewModel.clearDisplayData();

        mapWebView.getEngine().loadContent("");
        routeSectionTitle.setText("");
        transportTypeSectionTitle.setText("");
        statsSectionTitle.setText("");
        metricsSectionTitle.setText("");
        mapSectionTitle.setText("");
        descriptionSectionTitle.setText("");
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
                "Are you sure you want to delete \"" + selected.getTourName() + "\"?",
                () -> listViewModel.deleteTour(selected)
        );
    }
}