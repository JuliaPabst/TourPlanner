package org.tourplanner.view;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import org.tourplanner.viewmodel.SearchBarViewModel;

import java.net.URL;
import java.util.ResourceBundle;

public class SearchBarController implements Initializable {
    private final SearchBarViewModel viewModel;

    @FXML
    private TextField searchField;

    public SearchBarController(SearchBarViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Bindings.bindBidirectional(searchField.textProperty(), viewModel.searchQueryProperty());
    }

    @FXML
    public void onSearchClicked() {
        viewModel.performSearch();
    }
}
