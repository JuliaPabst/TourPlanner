package org.tourplanner;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class TourPlannerApplication extends Application {
    private static final Logger log = LogManager.getLogger(TourPlannerApplication.class);
    private ConfigurableApplicationContext springContext;
    public static HostServices HOST_SERVICES;

    @Override
    public void init() {
        log.info("Initializing Spring context");
        springContext = new SpringApplicationBuilder(TourPlannerConfig.class).run(getParameters().getRaw().toArray(new String[0]));
        log.info("Spring context initialized successfully.");
    }

    @Override
    public void start(Stage stage) throws IOException {
        log.info("Starting JavaFX application");
        FXMLLoader fxmlLoader = new FXMLLoader(TourPlannerApplication.class.getResource("main-view.fxml"));
        fxmlLoader.setControllerFactory(springContext::getBean);
        Parent root = fxmlLoader.load();
        showStage(stage, root);
        HOST_SERVICES = getHostServices();
        log.info("JavaFX application started");
    }

    public static void showStage(Stage stage, Parent root) {
        Scene scene = new Scene(root, 1080, 500);
        stage.setTitle("Tour Planner");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        log.info("Stopping application: closing Spring context and exiting JavaFX platform.");
        springContext.close();
        Platform.exit();
    }

    public static void main(String[] args) {
        log.info("Launching application");
        launch();
    }
}