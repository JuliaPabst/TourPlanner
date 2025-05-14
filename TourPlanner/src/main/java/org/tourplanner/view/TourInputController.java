package org.tourplanner.view;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import org.tourplanner.model.Tour;
import org.tourplanner.viewmodel.TourInputViewModel;

import java.net.URL;
import java.util.ResourceBundle;

public class TourInputController implements Initializable {
    private final TourInputViewModel viewModel;

    @FXML
    private TextField newTourNameField;

    @FXML
    private TextField newTourDistanceField;

    public TourInputController(TourInputViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Bindings.bindBidirectional(newTourNameField.textProperty(), viewModel.newTourNameProperty());
        Bindings.bindBidirectional(newTourDistanceField.textProperty(), viewModel.newTourDistanceProperty(), new javafx.util.converter.NumberStringConverter());

        viewModel.addTourCreatedListener(evt -> {
            Tour created = (Tour) evt.getNewValue();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Tour Created");
            alert.setHeaderText("New tour added successfully");
            alert.setContentText("Tour: " + created.name() + " (" + created.distance() + " km)");
            alert.showAndWait();
        });
    }

    @FXML
    public void onAddButtonClick(ActionEvent actionEvent) {
        try {
            viewModel.createNewTour();
        } catch(Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Could not create route.");
            alert.showAndWait();
        }
    }
}
