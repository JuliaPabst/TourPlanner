package org.tourplanner.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import org.springframework.stereotype.Controller;
import org.tourplanner.persistence.entity.Difficulty;
import org.tourplanner.view.util.ModalService;
import org.tourplanner.viewmodel.TourLogInputViewModel;
import org.tourplanner.utils.NumberTextFormatters;

import java.net.URL;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;

@Controller
public class TourLogInputController implements Initializable {
    private final TourLogInputViewModel viewModel;
    private Stage dialogStage;

    public TourLogInputController(TourLogInputViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    @FXML private DatePicker logDatePicker;
    @FXML private TextField usernameField;
    @FXML private TextField totalTimeField;
    @FXML private TextField totalDistanceField;
    @FXML private ComboBox<Difficulty> difficultyBox;
    @FXML private Spinner<Integer> ratingSpinner;
    @FXML private TextArea commentArea;
    @FXML private Button saveLogButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logDatePicker.valueProperty().bindBidirectional(viewModel.dateProperty());
        usernameField.textProperty().bindBidirectional(viewModel.usernameProperty());

        totalTimeField.setTextFormatter(NumberTextFormatters.forInteger(viewModel.totalTimeProperty()));
        totalDistanceField.setTextFormatter(NumberTextFormatters.forDouble(viewModel.totalDistanceProperty()));

        commentArea.textProperty().bindBidirectional(viewModel.commentProperty());

        difficultyBox.getItems().setAll(Difficulty.values());
        difficultyBox.valueProperty().bindBidirectional(viewModel.difficultyProperty());

        SpinnerValueFactory<Integer> factory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5, viewModel.ratingProperty().get());
        ratingSpinner.setValueFactory(factory);

        ratingSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            if(newVal != null) {
                viewModel.ratingProperty().set(newVal);
            }
        });
        viewModel.ratingProperty().addListener((obs, oldVal, newVal) -> {
            if(newVal != null && factory.getValue() != newVal) {
                factory.setValue((Integer) newVal);
            }
        });
    }

    @FXML
    public void onSaveLogClicked(ActionEvent actionEvent) {
        try {
            viewModel.saveOrUpdateLog();
            closeDialog();
        } catch(IllegalArgumentException | DateTimeParseException e) {
            ModalService.showInfoModal("Invalid Input", e.getMessage());
        } catch(Exception e) {
            ModalService.showInfoModal("Unexpected Error", "Something went wrong: " + e.getMessage());
        }
    }

    private void closeDialog() {
        if(dialogStage != null) {
            dialogStage.close();
        } else {
            ((Stage) saveLogButton.getScene().getWindow()).close();
        }
    }
}