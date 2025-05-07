package org.tourplanner.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class MainController {
    public TextField searchField;
    @FXML
    private Label welcomeText;

    @FXML
    protected void onSearch() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}