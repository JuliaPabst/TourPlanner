package org.tourplanner.viewmodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tourplanner.exception.ValidationException;
import org.tourplanner.persistence.entity.Difficulty;
import org.tourplanner.persistence.entity.Tour;
import org.tourplanner.persistence.entity.TourLog;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TourLogInputViewModelTest {

    private TourListViewModel tourListViewModel;
    private TourLogListViewModel logListViewModel;
    private TourLogInputViewModel viewModel;

    @BeforeEach
    void setup() {
        // Arrange: create mocks and inject into the ViewModel
        tourListViewModel = mock(TourListViewModel.class);
        logListViewModel = mock(TourLogListViewModel.class);
        viewModel = new TourLogInputViewModel(logListViewModel, tourListViewModel);
    }

    @Test
    void saveOrUpdateLog_throwsExceptionIfInvalidRating() {
        // Arrange
        Tour tour = new Tour();
        when(tourListViewModel.selectedTourProperty()).thenReturn(new javafx.beans.property.SimpleObjectProperty<>(tour));

        viewModel.usernameProperty().set("Julia");
        viewModel.totalTimeProperty().set(45);
        viewModel.totalDistanceProperty().set(7.0);
        viewModel.ratingProperty().set(0);  // invalid rating

        // Act + Assert
        Exception exception = assertThrows(ValidationException.class, () -> {
            viewModel.saveOrUpdateLog();
        });

        assertEquals("Rating must be between 1 and 5", exception.getMessage());
        verifyNoInteractions(logListViewModel); // ensure no log was added or updated
    }

    @Test
    void saveOrUpdateLog_throwsExceptionIfUsernameIsEmpty() {
        // Arrange
        Tour tour = new Tour();
        when(tourListViewModel.selectedTourProperty()).thenReturn(new javafx.beans.property.SimpleObjectProperty<>(tour));

        viewModel.usernameProperty().set("   ");
        viewModel.totalTimeProperty().set(30);
        viewModel.totalDistanceProperty().set(5.0);
        viewModel.ratingProperty().set(3);

        // Act + Assert
        Exception exception = assertThrows(ValidationException.class, () -> {
            viewModel.saveOrUpdateLog();
        });

        assertEquals("Username cannot be empty", exception.getMessage());
        verifyNoInteractions(logListViewModel);
    }
}
