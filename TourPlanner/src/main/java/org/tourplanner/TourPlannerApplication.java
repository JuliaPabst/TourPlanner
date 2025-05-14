package org.tourplanner;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.tourplanner.service.TourManager;
import org.tourplanner.view.*;
import org.tourplanner.viewmodel.MainViewModel;
import org.tourplanner.viewmodel.TourInputViewModel;
import org.tourplanner.viewmodel.TourListViewModel;
import org.tourplanner.viewmodel.SearchBarViewModel;

import java.io.IOException;

public class TourPlannerApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        TourManager tourManager = new TourManager();
        TourInputViewModel tourInputViewModel = new TourInputViewModel();
        MainViewModel mainViewModel = new MainViewModel(tourManager, tourInputViewModel);
        TourListViewModel tourListViewModel = new TourListViewModel(tourManager);
        SearchBarViewModel searchBarViewModel = new SearchBarViewModel(tourListViewModel);

        FXMLLoader fxmlLoader = new FXMLLoader(TourPlannerApplication.class.getResource("main-view.fxml"));
        fxmlLoader.setControllerFactory(controllerClass -> {
            if(controllerClass == MainController.class) {
                return new MainController(mainViewModel);
            } else if(controllerClass == TourInputController.class) {
                return new TourInputController(tourInputViewModel);
            } else if(controllerClass == TourListController.class) {
                return new TourListController(tourListViewModel);
            } else if(controllerClass == SearchBarController.class) {
                return new SearchBarController(searchBarViewModel);
            } else if(controllerClass == MenuBarController.class) {
                return new MenuBarController();
            } else if(controllerClass == TourDetailController.class) {
                return new TourDetailController();
            } else {
                throw new IllegalArgumentException("Unknown controller: " + controllerClass);
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