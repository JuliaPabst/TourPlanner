module org.tourplanner {
    // Spring Boot
    requires spring.core;
    requires spring.beans;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.data.jpa;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;

    // JavaFX
    requires javafx.fxml;
    requires static lombok;
    requires com.fasterxml.jackson.databind;
    requires javafx.web;
    requires java.net.http;
    requires java.desktop;
    requires jakarta.validation;

    opens org.tourplanner.view to javafx.fxml;
    opens org.tourplanner to javafx.fxml;
    opens org.tourplanner.persistence.entity;

    exports org.tourplanner;
    exports org.tourplanner.view;
    exports org.tourplanner.viewmodel;
    exports org.tourplanner.persistence.entity;
    exports org.tourplanner.persistence.repository;
    exports org.tourplanner.service;
    opens org.tourplanner.service to javafx.fxml;
    opens org.tourplanner.config;
}