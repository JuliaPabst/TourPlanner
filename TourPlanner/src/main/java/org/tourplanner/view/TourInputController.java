package org.tourplanner.view;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;
import org.springframework.stereotype.Controller;
import org.tourplanner.persistence.entity.TransportType;
import org.tourplanner.view.util.ModalService;
import org.tourplanner.viewmodel.TourInputViewModel;

import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class TourInputController implements Initializable {
    private final TourInputViewModel viewModel;
    private Stage dialogStage;
    private boolean listenersInitialized = false;

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
    @FXML private Button saveTourButton;

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
        newTourTransportTypeBox.getItems().setAll(
                "DRIVING_CAR",
                "DRIVING_HGV",
                "CYCLING_REGULAR",
                "CYCLING_ROAD",
                "CYCLING_MOUNTAIN",
                "CYCLING_ELECTRIC",
                "FOOT_WALKING",
                "FOOT_HIKING"
        );

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
    }

    @FXML
    public void onSaveButtonClick(ActionEvent actionEvent) {
        try {
            viewModel.saveOrUpdateTour();
            closeModal();
        }  catch (IllegalArgumentException e) {
            ModalService.showInfoModal("Invalid Input", e.getMessage());
        } catch (Exception e) {
            ModalService.showInfoModal(
                    "Unexpected Error",
                    "Something went wrong: " + e.getMessage()
            );
        }
    }

    private void closeModal() {
        ((Stage) saveTourButton.getScene().getWindow()).close();
    }

}
