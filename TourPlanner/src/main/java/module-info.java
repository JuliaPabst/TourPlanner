module org.tourplanner {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens org.tourplanner to javafx.fxml;
    exports org.tourplanner;
    exports org.tourplanner.view;
    opens org.tourplanner.view to javafx.fxml;
}