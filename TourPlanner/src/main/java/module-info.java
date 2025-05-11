module org.tourplanner {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires javafx.base;
    //requires javafx.swt;
    requires java.desktop;

    opens org.tourplanner to javafx.fxml;
    exports org.tourplanner;
    exports org.tourplanner.view;
    exports org.tourplanner.viewmodel;
    exports org.tourplanner.model;
    exports org.tourplanner.service;
    opens org.tourplanner.view to javafx.fxml;
}