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
    requires jakarta.persistence;
    requires static lombok;
    requires spring.data.jpa;

    opens org.tourplanner.view to javafx.fxml;
    opens org.tourplanner to javafx.fxml;

    exports org.tourplanner;
    exports org.tourplanner.view;
    exports org.tourplanner.viewmodel;
    exports org.tourplanner.persistence.entity;
    exports org.tourplanner.persistence.repository;
    exports org.tourplanner.service;
}