package org.tourplanner.view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import org.tourplanner.model.Route;
import org.tourplanner.viewmodel.RouteListViewModel;

import java.net.URL;
import java.util.ResourceBundle;

public class RouteListController implements Initializable {
    private final RouteListViewModel viewModel;

    public RouteListController(RouteListViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @FXML
    private ListView<Route> routeList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        routeList.setItems(viewModel.getRoutes());
    }
}
