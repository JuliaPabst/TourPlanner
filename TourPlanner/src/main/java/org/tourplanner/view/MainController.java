package org.tourplanner.view;

import org.tourplanner.model.Route;
import org.tourplanner.service.RouteManager;
import org.tourplanner.viewmodel.MainViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private final RouteManager routeManager = new RouteManager();

    public MainViewModel viewModel = new MainViewModel(routeManager);

    @FXML
    private TextField newRouteNameField;

    @FXML
    private TextField newRouteDistanceField;

    @FXML
    public ListView<Route> routeList;

    @FXML
    private TextField searchField;

    @FXML
    protected void onSearch() {
        String query = searchField.getText();
        viewModel.filterRoutesByName(query);
    }

    @FXML
    protected void onAddButtonClick() {
        try {
            int distance = Integer.parseInt(newRouteDistanceField.getText());
            viewModel.setNewRouteName(newRouteNameField.getText());
            viewModel.setNewRouteDistance(distance);
            viewModel.createNewRoute();
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please enter a valid number for distance.");
            alert.showAndWait();
        }
    }

    public void onExitButtonClick(ActionEvent actionEvent) {
        System.exit(0);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        routeList.setItems(routeManager.getRouteList());
        routeList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            viewModel.setSelectedRouteItem(newValue);
        });

        viewModel.addRouteCreatedListener(evt -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Route created");
            alert.setHeaderText("Route created successfully");
            alert.setContentText("The route '" + evt.getNewValue() + "' has been created.");
            alert.showAndWait();
        });
    }
}
