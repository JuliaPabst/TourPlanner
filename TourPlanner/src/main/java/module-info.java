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
    requires javafx.swing;
    requires javafx.graphics;

    // log4j2
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;

    // iText
    requires jakarta.annotation;
    requires layout;
    requires kernel;

    requires javafx.controls;
    requires io;
    requires spring.tx;

    opens org.tourplanner.view to javafx.fxml;
    opens org.tourplanner to javafx.fxml, org.apache.logging.log4j, org.apache.logging.log4j.core;
    opens org.tourplanner.persistence.entity;

    exports org.tourplanner;
    exports org.tourplanner.view;
    exports org.tourplanner.viewmodel;
    exports org.tourplanner.persistence.entity;
    exports org.tourplanner.persistence.repository;
    exports org.tourplanner.service;
    exports org.tourplanner.service.impl to spring.beans;
    opens org.tourplanner.service.impl to spring.core;
    opens org.tourplanner.service to javafx.fxml, spring.core;
    opens org.tourplanner.config;
    exports org.tourplanner.config;
    exports org.tourplanner.backup to com.fasterxml.jackson.databind;
}