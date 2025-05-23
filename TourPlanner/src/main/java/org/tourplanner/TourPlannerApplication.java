package org.tourplanner;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.tourplanner.model.Tour;
import org.tourplanner.service.TourManager;
import org.tourplanner.view.*;
import org.tourplanner.view.util.ModalService;
import org.tourplanner.utils.ViewModelInitializer;
import org.tourplanner.viewmodel.MainViewModel;
import org.tourplanner.viewmodel.TourInputViewModel;
import org.tourplanner.viewmodel.TourListViewModel;
import org.tourplanner.viewmodel.SearchBarViewModel;
import org.tourplanner.viewmodel.TourLogInputViewModel;
import org.tourplanner.service.TourLogManager;
import org.tourplanner.viewmodel.TourLogListViewModel;

import java.io.IOException;

public class TourPlannerApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Managers
        TourLogManager tourLogManager = new TourLogManager();
        TourManager tourManager = new TourManager(tourLogManager);

        // ViewModels
        TourListViewModel tourListViewModel = new TourListViewModel(tourManager, tourLogManager);
        TourInputViewModel tourInputViewModel = new TourInputViewModel(tourManager, tourListViewModel);
        MainViewModel mainViewModel = new MainViewModel(tourManager, tourInputViewModel);
        SearchBarViewModel searchBarViewModel = new SearchBarViewModel(tourListViewModel, tourLogManager);
        TourLogListViewModel tourLogListViewModel = new TourLogListViewModel(tourLogManager, tourListViewModel);
        TourLogInputViewModel tourLogInputViewModel = new TourLogInputViewModel(tourLogListViewModel, tourListViewModel);

        // Feedback-Listener
        ViewModelInitializer.setupListeners(tourInputViewModel);
        ViewModelInitializer.setupListeners(tourLogInputViewModel);

        // Main Scene
        FXMLLoader fxmlLoader = new FXMLLoader(TourPlannerApplication.class.getResource("main-view.fxml"));
        fxmlLoader.setControllerFactory(controllerClass -> {
            if(controllerClass == MainController.class) {
                return new MainController(mainViewModel, tourInputViewModel, tourLogInputViewModel);
            } else if(controllerClass == TourInputController.class) {
                return new TourInputController(tourInputViewModel);
            } else if(controllerClass == TourListController.class) {
                return new TourListController(tourInputViewModel, tourListViewModel, tourLogManager);
            } else if(controllerClass == SearchBarController.class) {
                return new SearchBarController(searchBarViewModel);
            } else if(controllerClass == TourDetailController.class) {
                return new TourDetailController(tourListViewModel, tourInputViewModel, tourLogManager);
            } else if(controllerClass == TourListItemController.class) {
                return new TourListItemController();
            } else if(controllerClass == TourLogListController.class) {
                return new TourLogListController(tourLogListViewModel, tourLogInputViewModel);
            } else {
                throw new IllegalArgumentException("Unknown controller: " + controllerClass);
            }
        });

        // Show
        Scene scene = new Scene(fxmlLoader.load(), 1080, 500);
        stage.setTitle("TourPlanner");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}