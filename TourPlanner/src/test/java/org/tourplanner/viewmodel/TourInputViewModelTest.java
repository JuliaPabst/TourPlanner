package org.tourplanner.viewmodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tourplanner.persistence.entity.Tour;
import org.tourplanner.persistence.entity.TransportType;
import org.tourplanner.service.TourManager;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TourInputViewModelTest {

    private TourManager mockTourManager;
    private TourListViewModel mockTourListViewModel;
    private TourInputViewModel viewModel;

    @BeforeEach
    void setUp() {
        mockTourManager = mock(TourManager.class);
        mockTourListViewModel = mock(TourListViewModel.class);
        viewModel = new TourInputViewModel(mockTourManager, mockTourListViewModel);
    }

    @Test
    void testInitialValues() {
        assertEquals("", viewModel.nameProperty().get());
        assertEquals("", viewModel.descriptionProperty().get());
        assertEquals("", viewModel.fromProperty().get());
        assertEquals("", viewModel.toProperty().get());
        assertEquals(0, viewModel.distanceProperty().get());
        assertEquals(0, viewModel.estimatedTimeProperty().get());
        assertEquals(TransportType.FOOT_HIKING, viewModel.transportTypeProperty().get());
    }

    @Test
    void testCreateNewTourFiresEventAndResetsFields() {
        // Arrange
        viewModel.nameProperty().set("Alpenrunde");
        viewModel.descriptionProperty().set("Through the Alps");
        viewModel.fromProperty().set("Innsbruck");
        viewModel.toProperty().set("Salzburg");
        viewModel.distanceProperty().set(150);
        viewModel.estimatedTimeProperty().set(300);
        viewModel.transportTypeProperty().set(TransportType.FOOT_HIKING);

        AtomicReference<Tour> createdTourRef = new AtomicReference<>();
        viewModel.addTourCreatedListener(evt -> {
            if ("newTour".equals(evt.getPropertyName())) {
                createdTourRef.set((Tour) evt.getNewValue());
            }
        });

        // Act
        viewModel.createNewTour();

        // Assert
        Tour createdTour = createdTourRef.get();
        assertNotNull(createdTour);
        assertEquals("Alpenrunde", createdTour.getTourName());
        assertEquals("Through the Alps", createdTour.getTourDescription());
        assertEquals("Innsbruck", createdTour.getFrom());
        assertEquals("Salzburg", createdTour.getTo());
        assertEquals(150, createdTour.getDistance());
        assertEquals(300, createdTour.getEstimatedTime());
        assertEquals(TransportType.FOOT_HIKING, createdTour.getTransportType());

        verify(mockTourManager).createNewTour(createdTour);

        // Verify reset
        assertEquals("", viewModel.nameProperty().get());
        assertEquals("", viewModel.descriptionProperty().get());
        assertEquals("", viewModel.fromProperty().get());
        assertEquals("", viewModel.toProperty().get());
        assertEquals(0, viewModel.distanceProperty().get());
        assertEquals(0, viewModel.estimatedTimeProperty().get());
        assertEquals(TransportType.FOOT_HIKING, viewModel.transportTypeProperty().get()); // back to default
    }

    @Test
    void testValidationFailsForMissingRequiredFields() {
        viewModel.nameProperty().set(""); // invalid
        viewModel.fromProperty().set("Vienna");
        viewModel.toProperty().set("Linz");
        viewModel.distanceProperty().set(10);
        viewModel.estimatedTimeProperty().set(20);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> viewModel.createNewTour());
        assertEquals("Required fields must not be empty.", ex.getMessage());
    }

    @Test
    void testValidationFailsForZeroDistanceOrTime() {
        viewModel.nameProperty().set("Short Trip");
        viewModel.fromProperty().set("Vienna");
        viewModel.toProperty().set("Graz");
        viewModel.distanceProperty().set(0); // invalid
        viewModel.estimatedTimeProperty().set(30);

        Exception ex1 = assertThrows(IllegalArgumentException.class, () -> viewModel.createNewTour());
        assertEquals("Distance must be greater than 0.", ex1.getMessage());

        viewModel.distanceProperty().set(100);
        viewModel.estimatedTimeProperty().set(0); // invalid

        Exception ex2 = assertThrows(IllegalArgumentException.class, () -> viewModel.createNewTour());
        assertEquals("Estimated time must be greater than 0.", ex2.getMessage());
    }

    @Test
    void testStartEditingPopulatesFields() {
        Tour tour = new Tour(
                null,
                "Donau Tour",
                "Along the Danube",
                "Vienna",
                "Linz",
                TransportType.FOOT_HIKING,
                200,
                240,
                null,
                null
        );

        viewModel.startEditing(tour);

        assertEquals("Donau Tour", viewModel.nameProperty().get());
        assertEquals("Along the Danube", viewModel.descriptionProperty().get());
        assertEquals("Vienna", viewModel.fromProperty().get());
        assertEquals("Linz", viewModel.toProperty().get());
        assertEquals(200, viewModel.distanceProperty().get());
        assertEquals(240, viewModel.estimatedTimeProperty().get());
        assertEquals(TransportType.FOOT_HIKING, viewModel.transportTypeProperty().get());
    }

    @Test
    void testSaveOrUpdateDelegatesUpdateIfEditingTourExists() {
        Tour oldTour = new Tour(
                null,
                "Test Old",
                "Old desc",
                "A",
                "B",
                TransportType.FOOT_HIKING,
                10,
                20,
                null,
                null
        );

        viewModel.startEditing(oldTour);

        viewModel.nameProperty().set("Updated");
        viewModel.descriptionProperty().set("Updated desc");
        viewModel.fromProperty().set("X");
        viewModel.toProperty().set("Y");
        viewModel.distanceProperty().set(99);
        viewModel.estimatedTimeProperty().set(999);
        viewModel.transportTypeProperty().set(TransportType.FOOT_HIKING);

        AtomicReference<Tour> updatedRef = new AtomicReference<>();
        viewModel.addTourEditedListener(evt -> {
            if ("tourEdited".equals(evt.getPropertyName())) {
                updatedRef.set((Tour) evt.getNewValue());
            }
        });

        viewModel.saveOrUpdateTour();

        Tour updatedTour = updatedRef.get();
        assertNotNull(updatedTour);
        assertEquals("Updated", updatedTour.getTourName());
        assertEquals("Updated desc", updatedTour.getTourDescription());
        assertEquals("X", updatedTour.getFrom());
        assertEquals("Y", updatedTour.getTo());
        assertEquals(99, updatedTour.getDistance());
        assertEquals(999, updatedTour.getEstimatedTime());
        assertEquals(TransportType.FOOT_HIKING, updatedTour.getTransportType());

        verify(mockTourManager).replaceTour(oldTour, updatedTour);
        verify(mockTourListViewModel).selectTour(updatedTour);
    }
}
