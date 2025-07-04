package org.tourplanner.view;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import lombok.Setter;

public class LoadingModalController {
    @FXML private Label loadingLabel;

    @Setter
    private Stage stage;

    public void close() {
        if (stage != null) stage.close();
    }
}
