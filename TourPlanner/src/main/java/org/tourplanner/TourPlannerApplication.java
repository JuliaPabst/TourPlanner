package org.tourplanner;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.tourplanner.service.RouteManager;
import org.tourplanner.view.MainController;
import org.tourplanner.view.RouteInputController;
import org.tourplanner.viewmodel.MainViewModel;
import org.tourplanner.viewmodel.RouteInputViewModel;

import java.io.IOException;

public class TourPlannerApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        RouteManager routeManager = new RouteManager();
        RouteInputViewModel routeInputViewModel = new RouteInputViewModel();
        MainViewModel mainViewModel = new MainViewModel(routeManager, routeInputViewModel);

        FXMLLoader fxmlLoader = new FXMLLoader(TourPlannerApplication.class.getResource("main-view.fxml"));
        fxmlLoader.setControllerFactory(controllerClass -> {
            if(controllerClass == MainController.class) {
                return new MainController(mainViewModel);
            } else if(controllerClass == RouteInputController.class) {
                return new RouteInputController(routeInputViewModel);
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