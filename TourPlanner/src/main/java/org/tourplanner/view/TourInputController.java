package org.tourplanner.view;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;
import lombok.Setter;
import org.springframework.stereotype.Controller;
import org.tourplanner.persistence.entity.TransportType;
import org.tourplanner.view.util.ModalService;
import org.tourplanner.viewmodel.TourInputViewModel;

import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class TourInputController implements Initializable {
    private final TourInputViewModel viewModel;
    @Setter
    private Stage dialogStage;
    private boolean listenersInitialized = false;

    public TourInputController(TourInputViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @FXML private TextField newTourNameField;
    @FXML private TextField newTourDescriptionField;
    @FXML private TextField newTourFromField;
    @FXML private TextField newTourToField;
    @FXML private ComboBox<TransportType> newTourTransportTypeBox;
    @FXML private Button saveTourButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Bind text fields
        Bindings.bindBidirectional(newTourNameField.textProperty(), viewModel.nameProperty());
        Bindings.bindBidirectional(newTourDescriptionField.textProperty(), viewModel.descriptionProperty());
        Bindings.bindBidirectional(newTourFromField.textProperty(), viewModel.fromProperty());
        Bindings.bindBidirectional(newTourToField.textProperty(), viewModel.toProperty());
        // Populate ComboBox with enum values
        newTourTransportTypeBox.getItems().setAll(TransportType.values());

        // Show user-friendly labels
        newTourTransportTypeBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(TransportType item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getLabel());
            }
        });
        newTourTransportTypeBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(TransportType item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getLabel());
            }
        });

        // Bind ComboBox to ViewModel
        newTourTransportTypeBox.valueProperty().bindBidirectional(viewModel.transportTypeProperty());
    }


    @FXML
    public void onSaveButtonClick(ActionEvent event) {
        Stage loadingStage = ModalService.showLoadingModal("Saving tour...");

        viewModel.loadingProperty().addListener((obs, wasLoading, isNowLoading) -> {
            if (!isNowLoading) {
                loadingStage.close();

                // If no error, close the tour input modal
                if (viewModel.errorMessageProperty().get() == null || viewModel.errorMessageProperty().get().isBlank()) {
                    closeModal();
                }
            }
        });

        viewModel.errorMessageProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isBlank()) {
                ModalService.showInfoModal("Error", newVal);
            }
        });

        viewModel.prepareAndRunSave();
    }


    private void closeModal() {
        ((Stage) saveTourButton.getScene().getWindow()).close();
    }
}
