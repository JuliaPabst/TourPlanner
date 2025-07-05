package org.tourplanner.viewmodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tourplanner.persistence.entity.Difficulty;
import org.tourplanner.persistence.entity.Tour;
import org.tourplanner.persistence.entity.TourLog;
import org.tourplanner.service.TourManager;
import org.tourplanner.service.TourLogManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TourListViewModelTest {

    private TourLogManager logManager;
    private ObservableList<Tour> tourList;
    private ObservableList<TourLog> logList;
    private TourListViewModel viewModel;

    @BeforeEach
    void setup() {
        // Arrange: Mock dependencies and set up data
        TourManager tourManager = mock(TourManager.class);
        logManager = mock(TourLogManager.class);

        tourList = FXCollections.observableArrayList();
        logList = FXCollections.observableArrayList();

        when(tourManager.getTourList()).thenReturn(tourList);
        when(logManager.getLogList()).thenReturn(logList);

        viewModel = new TourListViewModel(tourManager, logManager);
    }

    @Test
    void filterByFullText_matchesTourNameAndLogComment() {
        // Arrange
        Tour hikingTour = new Tour();
        hikingTour.setTourId(1);
        hikingTour.setTourName("Mountain Hike");

        Tour bikeTour = new Tour();
        bikeTour.setTourId(2);
        bikeTour.setTourName("City Bike Tour");

        tourList.addAll(hikingTour, bikeTour);

        TourLog log = new TourLog();
        log.setTour(hikingTour);
        log.setComment("Awesome view from the top!");
        log.setDifficulty(Difficulty.MEDIUM);

        logList.add(log);

        // Act
        viewModel.filterByFullText("view", logManager);

        // Assert
        List<Tour> filtered = viewModel.getTours();
        assertEquals(1, filtered.size());
        assertEquals(hikingTour, filtered.get(0));
    }

    @Test
    void clearDisplayData_resetsAllLabels() {
        // Arrange
        viewModel.fromLabelProperty().set("From: A");
        viewModel.toLabelProperty().set("To: B");
        viewModel.transportTypeLabelProperty().set("Car");
        viewModel.distanceLabelProperty().set("15 km");
        viewModel.timeLabelProperty().set("30 min");
        viewModel.descriptionTextProperty().set("A nice tour");
        viewModel.popularityTextProperty().set("★★★");
        viewModel.childFriendlyTextProperty().set("★★");
        viewModel.mapHtmlContentProperty().set("some HTML map");

        // Act
        viewModel.clearDisplayData();

        // Assert
        assertEquals("", viewModel.fromLabelProperty().get());
        assertEquals("", viewModel.toLabelProperty().get());
        assertEquals("", viewModel.transportTypeLabelProperty().get());
        assertEquals("", viewModel.distanceLabelProperty().get());
        assertEquals("", viewModel.timeLabelProperty().get());
        assertEquals("", viewModel.descriptionTextProperty().get());
        assertEquals("", viewModel.popularityTextProperty().get());
        assertEquals("", viewModel.childFriendlyTextProperty().get());
        assertTrue(viewModel.mapHtmlContentProperty().get().contains("No map selected"));
    }
}
