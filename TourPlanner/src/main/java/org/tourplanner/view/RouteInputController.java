package org.tourplanner.view;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.tourplanner.model.Route;
import org.tourplanner.viewmodel.RouteInputViewModel;

import java.net.URL;
import java.util.ResourceBundle;

public class RouteInputController implements Initializable {
    private final RouteInputViewModel viewModel;

    @FXML
    private TextField newRouteNameField;

    @FXML
    private TextField newRouteDistanceField;

    public RouteInputController(RouteInputViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Bindings.bindBidirectional(newRouteNameField.textProperty(), viewModel.newRouteNameProperty());
        Bindings.bindBidirectional(newRouteDistanceField.textProperty(), viewModel.newRouteDistanceProperty(), new javafx.util.converter.NumberStringConverter());

        viewModel.addRouteCreatedListener(evt -> {
            Route created = (Route) evt.getNewValue();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Route Created");
            alert.setHeaderText("New route added successfully");
            alert.setContentText("Route: " + created.routeName() + " (" + created.distance() + " km)");
            alert.showAndWait();
        });
    }

    @FXML
    public void onAddButtonClick(ActionEvent actionEvent) {
        try {
            viewModel.createNewRoute();
        } catch(Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Could not create route.");
            alert.showAndWait();
        }
    }
}
