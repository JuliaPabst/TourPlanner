module org.tourplanner {
    //Java FX
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires java.desktop;

    // Spring Boot
    requires spring.core;
    requires spring.beans;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.boot;

    opens org.tourplanner.view to javafx.fxml;
    opens org.tourplanner to javafx.fxml;

    exports org.tourplanner;
    exports org.tourplanner.view;
    exports org.tourplanner.viewmodel;
    exports org.tourplanner.model;
    exports org.tourplanner.service;
}