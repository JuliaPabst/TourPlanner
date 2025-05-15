package org.tourplanner.view;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;
import org.tourplanner.model.Tour;
import org.tourplanner.model.TransportType;
import org.tourplanner.viewmodel.TourInputViewModel;

import java.net.URL;
import java.util.ResourceBundle;

public class TourInputController implements Initializable {
    private final TourInputViewModel viewModel;
    private Stage dialogStage;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public TourInputController(TourInputViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @FXML private TextField newTourNameField;
    @FXML private TextField newTourDescriptionField;
    @FXML private TextField newTourFromField;
    @FXML private TextField newTourToField;
    @FXML private ComboBox<String> newTourTransportTypeBox;
    @FXML private TextField newTourDistanceField;
    @FXML private TextField newTourEstimatedTimeField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Bind text fields
        Bindings.bindBidirectional(newTourNameField.textProperty(), viewModel.nameProperty());
        Bindings.bindBidirectional(newTourDescriptionField.textProperty(), viewModel.descriptionProperty());
        Bindings.bindBidirectional(newTourFromField.textProperty(), viewModel.fromProperty());
        Bindings.bindBidirectional(newTourToField.textProperty(), viewModel.toProperty());
        Bindings.bindBidirectional(newTourDistanceField.textProperty(), viewModel.distanceProperty(), new NumberStringConverter());
        Bindings.bindBidirectional(newTourEstimatedTimeField.textProperty(), viewModel.estimatedTimeProperty(), new NumberStringConverter());

        // Initialize transport type box
        newTourTransportTypeBox.getItems().setAll("BIKE", "HIKE", "RUNNING");
        newTourTransportTypeBox.getSelectionModel().select(viewModel.transportTypeProperty().get().name());

        // Keep ViewModel in sync with ComboBox selection
        newTourTransportTypeBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                viewModel.transportTypeProperty().set(TransportType.valueOf(newVal));
            }
        });

        viewModel.transportTypeProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                newTourTransportTypeBox.getSelectionModel().select(newVal.name());
            }
        });

        // Success alert on tour creation
        viewModel.addTourCreatedListener(evt -> {
            Tour created = (Tour) evt.getNewValue();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Tour Created");
            alert.setHeaderText("New tour added successfully");
            alert.setContentText("Tour: " + created.name() + " from " + created.from() + " to " + created.to());
            alert.showAndWait();
        });
    }

    @FXML
    public void onAddButtonClick(ActionEvent actionEvent) {
        try {
            viewModel.createNewTour();
            if (dialogStage != null) {
                dialogStage.close();
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Could not create tour: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
