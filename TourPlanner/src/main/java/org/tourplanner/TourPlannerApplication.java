package org.tourplanner;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.tourplanner.service.RouteManager;
import org.tourplanner.view.*;
import org.tourplanner.viewmodel.MainViewModel;
import org.tourplanner.viewmodel.RouteInputViewModel;
import org.tourplanner.viewmodel.RouteListViewModel;
import org.tourplanner.viewmodel.SearchBarViewModel;

import java.io.IOException;

public class TourPlannerApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        RouteManager routeManager = new RouteManager();
        RouteInputViewModel routeInputViewModel = new RouteInputViewModel();
        MainViewModel mainViewModel = new MainViewModel(routeManager, routeInputViewModel);
        RouteListViewModel routeListViewModel = new RouteListViewModel(routeManager);
        SearchBarViewModel searchBarViewModel = new SearchBarViewModel(routeListViewModel);

        FXMLLoader fxmlLoader = new FXMLLoader(TourPlannerApplication.class.getResource("main-view.fxml"));
        fxmlLoader.setControllerFactory(controllerClass -> {
            if(controllerClass == MainController.class) {
                return new MainController(mainViewModel);
            } else if(controllerClass == RouteInputController.class) {
                return new RouteInputController(routeInputViewModel);
            } else if(controllerClass == RouteListController.class) {
                return new RouteListController(routeListViewModel);
            } else if(controllerClass == SearchBarController.class) {
                return new SearchBarController(searchBarViewModel);
            } else if(controllerClass == MenuBarController.class) {
                return new MenuBarController();
            } else {
                throw new IllegalArgumentException("Unknown contorller: " + controllerClass);
            }
        });

        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("TourPlanner");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}