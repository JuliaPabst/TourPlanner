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
